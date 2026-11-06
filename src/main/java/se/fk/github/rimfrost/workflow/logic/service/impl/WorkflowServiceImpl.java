package se.fk.github.rimfrost.workflow.logic.service.impl;

import java.time.OffsetDateTime;
import java.util.UUID;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import se.fk.github.rimfrost.workflow.integration.KafkaProducer;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.dto.RollIYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningAdapterException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningNotFoundException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningProcessStartException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningUpdateException;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.logic.util.LogicMapper;
import se.fk.github.rimfrost.workflow.storage.WorkflowDataStorage;
import se.fk.rimfrost.framework.erbjudande.topic.adapter.ErbjudandeTopicAdapter;
import se.fk.rimfrost.framework.erbjudande.topic.exception.ErbjudandeTopicException;
import se.fk.rimfrost.framework.handlaggning.adapter.HandlaggningAdapter;
import se.fk.rimfrost.framework.handlaggning.exception.HandlaggningException;
import se.fk.rimfrost.framework.handlaggning.model.Handlaggning;
import se.fk.rimfrost.framework.handlaggning.model.HandlaggningUpdate;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggning;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggningUpdate;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableRollIYrkande;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableSakfragaStallningstagandeContainer;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableYrkande;
import se.fk.rimfrost.framework.handlaggning.model.RollIYrkande;
import se.fk.rimfrost.framework.handlaggning.model.SakfragaStallningstagandeContainer;

@ApplicationScoped
public class WorkflowServiceImpl implements WorkflowService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowServiceImpl.class);

   @Inject
   private LogicMapper mapper;

   @Inject
   KafkaProducer producer;

   @Inject
   HandlaggningAdapter handlaggningAdapter;

   @Inject
   ErbjudandeTopicAdapter erbjudandeTopicAdapter;

   @Inject
   WorkflowDataStorage storage;

   @ConfigProperty(name = "rimfrost.yrkande.default.yrkande-status.id")
   String yrkandeStatusId;

   @ConfigProperty(name = "rimfrost.handlaggning.default.handlaggning-id-typ")
   String handlaggningIdTyp;

   @Override
   public YrkandeCreateResponse createYrkande(YrkandeCreateRequest request)
   {
      var handlaggningUpdate = createHandlaggningUpdate(request);
      UUID handlaggningId = handlaggningUpdate.handlaggning().id();

      var erbjudandeTopic = getErbjudandeTopic(request.ingangtypId());

      storeHandlaggningReplyTopic(handlaggningId, request.replyTo());

      try
      {
         sendHandlaggningUpdate(handlaggningUpdate);
      }
      catch (HandlaggningUpdateException e)
      {
         LOGGER.error("Failed to create handlaggning", e);

         tryDeleteHandlaggningReplyTopic(handlaggningId);

         throw e;
      }

      try
      {
         startHandlaggningProcess(erbjudandeTopic, handlaggningId);
      }
      catch (HandlaggningProcessStartException e)
      {
         LOGGER.error("Failed to start handlaggning process for handlaggning id: {}", handlaggningId, e);
      }

      return ImmutableYrkandeCreateResponse.builder()
            .handlaggning(mapper.toHandlaggningDTO(handlaggningUpdate.handlaggning()))
            .build();
   }

   @Override
   public HandlaggningDTO restartProcess(UUID handlaggningId, String replyTo)
   {
      var handlaggning = fetchHandlaggning(handlaggningId);

      storeHandlaggningReplyTopic(handlaggningId, replyTo);

      var erbjudandeTopic = getErbjudandeTopic(handlaggning.yrkande().ingangtypId());

      startHandlaggningProcess(erbjudandeTopic, handlaggningId);

      return mapper.toHandlaggningDTO(handlaggning);
   }

   private Handlaggning fetchHandlaggning(UUID handlaggningId)
   {
      try
      {
         return handlaggningAdapter.readHandlaggning(handlaggningId);
      }
      catch (HandlaggningException e)
      {
         if (e.getErrorType() == HandlaggningException.ErrorType.NOT_FOUND)
         {
            throw new HandlaggningNotFoundException(e);
         }
         throw new HandlaggningAdapterException(e);
      }
   }

   @Override
   public void handlaggningDone(HandlaggningResponseDTO response)
   {
      LOGGER.info("Handlaggning response received for handlaggning id: {}", response.handlaggningId());

      var errorInfo = response.error();
      if (errorInfo != null)
      {
         LOGGER.error("Handlaggning failed with error code {} and message {}", errorInfo.felkod(),
               errorInfo.felmeddelande());
      }

      try
      {
         String replyTopic = getHandlaggningReplyTopic(response.handlaggningId());
         producer.sendHandlaggningDone(response.handlaggningId(), replyTopic);
      }
      catch (Exception e)
      {
         LOGGER.error("Failed to send handlaggning done message", e);
         return;
      }

      tryDeleteHandlaggningReplyTopic(response.handlaggningId());
   }

   @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "False positive for handlaggningIdVarde and uppgift params that allow null")
   private HandlaggningUpdate createHandlaggningUpdate(YrkandeCreateRequest request)
   {
      var yrkandeId = UUID.randomUUID();

      var yrkande = ImmutableYrkande.builder()
            .id(yrkandeId)
            .version(1)
            .ingangtypId(request.ingangtypId())
            .yrkandeDatum(OffsetDateTime.now())
            .yrkandeStatus(yrkandeStatusId)
            .yrkandeFrom(request.yrkandeFrom())
            .yrkandeTom(request.yrkandeTom())
            .avsikt(request.avsiktsId())
            .rollerIYrkande(request.rollerIYrkande().stream().map(r -> createRollIYrkande(r, yrkandeId)).toList())
            .sakfragorStallningstaganden(
                  request.sakfragorStallningstaganden().stream().map(this::createSakfragaStallningstagande).toList())
            .build();

      var handlaggning = ImmutableHandlaggning.builder()
            .id(UUID.randomUUID())
            .version(1)
            .yrkande(yrkande)
            .handlaggningIdTyp(handlaggningIdTyp)
            .handlaggningIdVarde(null)
            .skapadTS(OffsetDateTime.now())
            .handlaggningspecifikationId(request.handlaggningspecifikationId())
            .build();

      return ImmutableHandlaggningUpdate.builder()
            .handlaggning(handlaggning)
            .uppgift(null)
            .build();
   }

   private RollIYrkande createRollIYrkande(RollIYrkandeCreateRequest request, UUID yrkandeId)
   {
      return ImmutableRollIYrkande.builder()
            .id(UUID.randomUUID())
            .individ(mapper.toIdtyp(request.individ()))
            .yrkandeRollId(request.yrkandeRollId())
            .avserYrkande(yrkandeId)
            .build();
   }

   private SakfragaStallningstagandeContainer createSakfragaStallningstagande(SakfragaStallningstagandeCreateRequest request)
   {
      return ImmutableSakfragaStallningstagandeContainer.builder()
            .id(UUID.randomUUID())
            .objektTypId(request.objektTypId())
            .data(request.data())
            .build();
   }

   private String getErbjudandeTopic(String erbjudandeId)
   {
      try
      {
         return erbjudandeTopicAdapter.getTopic(erbjudandeId);
      }
      catch (ErbjudandeTopicException e)
      {
         LOGGER.error("Failed to read process topic retrieve erbjudande topic", e);

         throw new ErbjudandeTopicReadException(e);
      }
   }

   private void sendHandlaggningUpdate(HandlaggningUpdate handlaggningUpdate)
   {
      try
      {
         handlaggningAdapter.updateHandlaggning(handlaggningUpdate);
      }
      catch (HandlaggningException e)
      {
         LOGGER.error("Failed to send handlaggning update", e);

         throw new HandlaggningUpdateException(e);
      }
   }

   private void startHandlaggningProcess(String processTopic, UUID handlaggningId)
   {
      try
      {
         producer.sendRequestMessage(processTopic, handlaggningId);
      }
      catch (Exception e)
      {
         LOGGER.error("Failed to start handlaggning process for handlaggning with id: {} and process topic: {}", handlaggningId,
               processTopic, e);

         throw new HandlaggningProcessStartException(e);
      }
   }

   private void storeHandlaggningReplyTopic(UUID handlaggningId, String replyTopic)
   {
      try
      {
         storage.storeHandlaggningReplyTopic(handlaggningId, replyTopic);
      }
      catch (Exception e)
      {
         LOGGER.error("Failed to write handlaggning reply topic to storage", e);

         throw new HandlaggningReplyTopicWriteException(e);
      }
   }

   private String getHandlaggningReplyTopic(UUID handlaggningId)
   {
      try
      {
         return storage.getHandlaggningReplyTopic(handlaggningId);
      }
      catch (Exception e)
      {
         LOGGER.error("Failed to read handlaggning reply topic from storage", e);

         throw new HandlaggningReplyTopicReadException(e);
      }
   }

   private void tryDeleteHandlaggningReplyTopic(UUID handlaggningId)
   {
      try
      {
         storage.deleteHandlaggningReplyTopic(handlaggningId);
      }
      catch (Exception e)
      {
         LOGGER.error("Failed to delete handlaggning reply topic from storage for handlaggning id: {}", handlaggningId, e);
      }
   }
}
