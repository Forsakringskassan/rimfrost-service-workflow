package se.fk.github.rimfrost.workflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Providers;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.fk.github.rimfrost.workflow.integration.KafkaProducer;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningProcessStartException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningUpdateException;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.storage.WorkflowDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createHandlaggningResponseDTO;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createPostYrkandeRequest;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createYrkandeCreateRequest;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockTestResource.class)
})
public class WorkflowServiceTest extends WorkflowTestBase
{
   private static WireMockServer wireMockServer;

   @BeforeAll
   static void setUp()
   {
      wireMockServer = WireMockTestResource.getWireMockServer();
   }

   @AfterAll
   static void tearDown()
   {
      wireMockServer = null;
   }

   @Inject
   WorkflowService workflowService;

   @InjectMock
   WorkflowDataStorage workflowDataStorage;

   @InjectMock
   KafkaProducer kafkaProducer;

   @ConfigProperty(name = "rimfrost.yrkande.default.yrkande-status.id")
   String yrkandeStatusId;

   @ConfigProperty(name = "rimfrost.producerat-resultat.default.yrkande-status.id")
   String produceratResultatStatusId;

   @Test
   void should_throw_erbjudande_topic_read_exception_on_erbjudande_topic_read_failure_during_create_yrkande()
   {
      wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/topic/.+"))
            .willReturn(WireMock.aResponse().withStatus(500)));
      assertThrows(ErbjudandeTopicReadException.class, () -> workflowService.createYrkande(createYrkandeCreateRequest()));
   }

   @Test
   void should_throw_handlaggning_reply_topic_write_exception_on_storage_write_failure_during_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doThrow(new IllegalStateException()).when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(),
            eq(request.replyTo()));
      assertThrows(HandlaggningReplyTopicWriteException.class, () -> workflowService.createYrkande(request));
   }

   @Test
   void should_throw_handlaggning_update_exception_on_handlaggning_update_failure_during_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doNothing().when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(), eq(request.replyTo()));
      wireMockServer.stubFor(WireMock.put(WireMock.urlPathMatching("/handlaggning/.+"))
            .willReturn(WireMock.aResponse().withStatus(500)));
      assertThrows(HandlaggningUpdateException.class, () -> workflowService.createYrkande(request));
      Mockito.verify(workflowDataStorage, Mockito.times(1)).deleteHandlaggningReplyTopic(Mockito.any());
   }

   @Test
   void should_throw_handlaggning_update_exception_on_handlaggning_update_failure_with_storage_delete_failure_during_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doNothing().when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(), eq(request.replyTo()));
      wireMockServer.stubFor(WireMock.put(WireMock.urlPathMatching("/handlaggning/.+"))
            .willReturn(WireMock.aResponse().withStatus(500)));
      Mockito.doThrow(new IllegalStateException()).when(workflowDataStorage).deleteHandlaggningReplyTopic(Mockito.any());
      assertThrows(HandlaggningUpdateException.class, () -> workflowService.createYrkande(request));
      Mockito.verify(workflowDataStorage, Mockito.times(1)).deleteHandlaggningReplyTopic(Mockito.any());
   }

   @Test
   void should_return_yrkande_create_response_on_handlaggning_process_start_failure_during_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doNothing().when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(), eq(request.replyTo()));
      Mockito.doThrow(new IllegalStateException()).when(kafkaProducer).sendRequestMessage(Mockito.any(), Mockito.any());
      var response = workflowService.createYrkande(request);
      Mockito.verify(workflowDataStorage, Mockito.never()).deleteHandlaggningReplyTopic(Mockito.any());
      assertNotNull(response.handlaggning());
   }

   @Test
   void should_return_yrkande_create_response_on_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doNothing().when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(), eq(request.replyTo()));
      Mockito.doNothing().when(kafkaProducer).sendRequestMessage(eq("test"), Mockito.any());
      var response = workflowService.createYrkande(request);

      Mockito.verify(workflowDataStorage, Mockito.never()).deleteHandlaggningReplyTopic(Mockito.any());
      assertNotNull(response.handlaggning());

      var handlaggning = response.handlaggning();

      assertNotNull(handlaggning.id());
      assertEquals(1, handlaggning.version());
      assertNull(handlaggning.processinstansId());
      assertEquals(request.handlaggningspecifikationId(), handlaggning.handlaggningspecifikationId());
      assertNotNull(handlaggning.skapadTS());
      assertNull(handlaggning.avslutadTS());
      assertNotNull(handlaggning.yrkande());
      Mockito.verify(workflowDataStorage, Mockito.times(1)).storeHandlaggningReplyTopic(eq(handlaggning.id()),
            eq(request.replyTo()));
      Mockito.verify(kafkaProducer, Mockito.times(1)).sendRequestMessage(eq("test"), eq(handlaggning.id()));

      var yrkande = handlaggning.yrkande();
      assertNotNull(yrkande.id());
      assertEquals(1, yrkande.version());
      assertEquals(request.erbjudandeId(), yrkande.erbjudandeId());
      assertNotNull(yrkande.yrkandedatum());
      assertNotNull(yrkande.yrkandeFrom());
      assertNotNull(yrkande.yrkandeTom());
      assertEquals(yrkandeStatusId, yrkande.yrkandestatus());
      assertEquals(request.avsiktsId(), yrkande.avsikt());
      assertNull(yrkande.beslut());
      assertEquals(1, yrkande.individYrkandeRoll().size());
      assertEquals(1, yrkande.produceradeResultat().size());

      var individYrkandeRoll = yrkande.individYrkandeRoll().getFirst();
      var expectedIndividYrkandeRoll = request.individYrkandeRoller().getFirst();
      assertNotNull(individYrkandeRoll.individ());
      assertEquals(expectedIndividYrkandeRoll.individ().typId(), individYrkandeRoll.individ().typId());
      assertEquals(expectedIndividYrkandeRoll.individ().varde(), individYrkandeRoll.individ().varde());
      assertEquals(expectedIndividYrkandeRoll.yrkandeRollId(), individYrkandeRoll.yrkandeRollId());

      var produceratResultat = yrkande.produceradeResultat().getFirst();
      var produceratResultatRequest = request.produceradeResultat().getFirst();
      assertNotNull(produceratResultat.id());
      assertEquals(1, produceratResultat.version());
      assertEquals(produceratResultatRequest.franOchMed(), produceratResultat.franOchMed());
      assertEquals(produceratResultatRequest.tillOchMed(), produceratResultat.tillOchMed());
      assertEquals(produceratResultatStatusId, produceratResultat.yrkandestatus());
      assertNull(produceratResultat.avslagsanledning());
      assertEquals(produceratResultatRequest.typ(), produceratResultat.typ());
      assertEquals(produceratResultatRequest.data(), produceratResultatRequest.data());
   }

   @Test
   void should_not_delete_handlaggning_reply_topic_on_handlaggning_reply_topic_read_failure_during_handlaggning_response()
   {
      var handlaggningResponse = createHandlaggningResponseDTO();
      Mockito.when(workflowDataStorage.getHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId())))
            .thenThrow(new IllegalStateException());
      workflowService.handlaggningDone(handlaggningResponse);
      Mockito.verify(kafkaProducer, Mockito.never()).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()),
            Mockito.any());
      Mockito.verify(workflowDataStorage, Mockito.never())
            .deleteHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId()));
   }

   @Test
   void should_not_delete_handlaggning_reply_topic_on_handlaggning_done_msg_send_failure_during_handlaggning_response()
   {
      var handlaggningResponse = createHandlaggningResponseDTO();
      var replyTopic = "replyTopic";
      Mockito.when(workflowDataStorage.getHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId())))
            .thenReturn(replyTopic);
      Mockito.doThrow(new IllegalStateException()).when(kafkaProducer)
            .sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()), eq(replyTopic));
      workflowService.handlaggningDone(handlaggningResponse);
      Mockito.verify(kafkaProducer, Mockito.times(1)).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()),
            eq(replyTopic));
      Mockito.verify(workflowDataStorage, Mockito.never())
            .deleteHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId()));
   }

   @Test
   void should_delete_handlaggning_reply_topic_on_handlaggning_response()
   {
      var handlaggningResponse = createHandlaggningResponseDTO();
      var replyTopic = "replyTopic";
      Mockito.when(workflowDataStorage.getHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId())))
            .thenReturn(replyTopic);
      Mockito.doNothing().when(kafkaProducer).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()), eq(replyTopic));
      workflowService.handlaggningDone(handlaggningResponse);
      Mockito.verify(kafkaProducer, Mockito.times(1)).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()),
            eq(replyTopic));
      Mockito.verify(workflowDataStorage, Mockito.times(1))
            .deleteHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId()));
   }

   @Test
   void should_not_crash_exception_on_delete_handlaggning_reply_topic_exception_during_handlaggning_response()
   {
      var handlaggningResponse = createHandlaggningResponseDTO();
      var replyTopic = "replyTopic";
      Mockito.when(workflowDataStorage.getHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId())))
            .thenReturn(replyTopic);
      Mockito.doNothing().when(kafkaProducer).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()), eq(replyTopic));
      Mockito.doThrow(new IllegalStateException()).when(workflowDataStorage)
            .deleteHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId()));
      workflowService.handlaggningDone(handlaggningResponse);
      Mockito.verify(kafkaProducer, Mockito.times(1)).sendHandlaggningDone(eq(handlaggningResponse.handlaggningId()),
            eq(replyTopic));
      Mockito.verify(workflowDataStorage, Mockito.times(1))
            .deleteHandlaggningReplyTopic(eq(handlaggningResponse.handlaggningId()));
   }
}
