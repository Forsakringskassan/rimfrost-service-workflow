package se.fk.github.rimfrost.workflow.presentation.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.MDCKeys;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.presentation.util.PresentationMapper;
import se.fk.rimfrost.HandlaggningResponseMessagePayload;

@ApplicationScoped
public class KafkaConsumer
{

   private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

   @Inject
   PresentationMapper mapper;

   @Inject
   WorkflowService workflowService;

   @Incoming("handlaggning-responses")
   public void onHandlaggningResponse(HandlaggningResponseMessagePayload handlaggningResponseMessagePayload)
   {
      try
      {
         if (handlaggningResponseMessagePayload == null || handlaggningResponseMessagePayload.getData() == null)
         {
            LOGGER.error("Received invalid handlaggning response message: {}", handlaggningResponseMessagePayload);
            return;
         }

         LOGGER.info("Received handlaggning done message: {}", handlaggningResponseMessagePayload);

         MDC.put(MDCKeys.PROCESSID.name(), handlaggningResponseMessagePayload.getData().getHandlaggningId());
         var responseMessageData = handlaggningResponseMessagePayload.getData();

         HandlaggningResponseDTO handlaggningResponse;
         try
         {
            handlaggningResponse = mapper.toHandlaggningResponseDTO(responseMessageData);
         }
         catch (Exception e)
         {
            LOGGER.error("Failed to construct handlaggning response DTO from response message for handlaggning id: {}",
                  responseMessageData.getHandlaggningId(), e);
            return;
         }

         workflowService.handlaggningDone(handlaggningResponse);
      }
      catch (Exception e)
      {
         LOGGER.error("Handlaggning response request terminated due to unexpected exception", e);
      }
   }

}
