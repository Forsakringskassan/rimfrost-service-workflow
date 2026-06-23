package se.fk.github.rimfrost.workflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Providers;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.fk.github.rimfrost.workflow.integration.KafkaProducer;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningAdapterException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningNotFoundException;
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
   @DisplayName("Erbjudande-topic-fel ger ErbjudandeTopicReadException vid skapande av yrkande")
   void should_throw_erbjudande_topic_read_exception_on_erbjudande_topic_read_failure_during_create_yrkande()
   {
      wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/topic/.+"))
            .willReturn(WireMock.aResponse().withStatus(500)));
      assertThrows(ErbjudandeTopicReadException.class, () -> workflowService.createYrkande(createYrkandeCreateRequest()));
   }

   @Test
   @DisplayName("Lagringsfel av replyTo-topic ger HandlaggningReplyTopicWriteException vid skapande av yrkande")
   void should_throw_handlaggning_reply_topic_write_exception_on_storage_write_failure_during_create_yrkande()
   {
      var request = createYrkandeCreateRequest();
      Mockito.doThrow(new IllegalStateException()).when(workflowDataStorage).storeHandlaggningReplyTopic(Mockito.any(),
            eq(request.replyTo()));
      assertThrows(HandlaggningReplyTopicWriteException.class, () -> workflowService.createYrkande(request));
   }

   @Test
   @DisplayName("Handläggning update-fel ger HandlaggningUpdateException och rensar lagrad replyTo-topic")
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
   @DisplayName("Handläggning update-fel ger HandlaggningUpdateException även om borttagning av replyTo-topic misslyckas")
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
   @DisplayName("Process-startsfel loggas och yrkande-svar returneras ändå vid skapande av yrkande")
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
   @DisplayName("Yrkande skapas framgångsrikt och handläggning returneras med korrekt data")
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
   @DisplayName("Läsfel av replyTo-topic avbryter notifieringsflödet utan att radera replyTo-topicen")
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
   @DisplayName("Send error av avslutningsmeddelande avbryter flödet utan att radera replyTo-topic")
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
   @DisplayName("replyTo-topic raderas efter framgångsrikt skickande av avslutningsmeddelande")
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
   @DisplayName("Borttagningsfel av replyTo-topic loggas utan att krascha avslutningsflödet")
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

   @Test
   @DisplayName("FKPOC-869-AC2: Handläggning not-found ger HandlaggningNotFoundException")
   void should_throw_handlaggning_not_found_exception_on_not_found_during_restart_process()
   {
      wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/handlaggning/.+"))
            .willReturn(WireMock.aResponse().withStatus(404)));
      var handlaggningId = UUID.randomUUID();
      assertThrows(HandlaggningNotFoundException.class, () -> workflowService.restartProcess(handlaggningId, null));
   }

   @Test
   @DisplayName("Adapter-fel (ej 404) vid hämtning av handläggning ger HandlaggningAdapterException")
   void should_throw_handlaggning_adapter_exception_on_non_not_found_error_during_restart_process()
   {
      wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/handlaggning/.+"))
            .willReturn(WireMock.aResponse().withStatus(500)));
      var handlaggningId = UUID.randomUUID();
      assertThrows(HandlaggningAdapterException.class, () -> workflowService.restartProcess(handlaggningId, null));
   }

   @Test
   @DisplayName("FKPOC-869-AC3: replyTo-topic lagras när replyTo är angivet")
   void should_store_reply_topic_when_reply_to_is_present_during_restart_process()
   {
      var handlaggningId = UUID.randomUUID();
      var replyTo = "my-replyTo-topic";
      workflowService.restartProcess(handlaggningId, replyTo);
      Mockito.verify(workflowDataStorage, Mockito.times(1)).storeHandlaggningReplyTopic(eq(handlaggningId), eq(replyTo));
   }

   @Test
   @DisplayName("FKPOC-869-AC3: replyTo-topic lagras inte när replyTo saknas")
   void should_not_store_reply_topic_when_reply_to_is_absent_during_restart_process()
   {
      var handlaggningId = UUID.randomUUID();
      workflowService.restartProcess(handlaggningId, null);
      Mockito.verify(workflowDataStorage, Mockito.never()).storeHandlaggningReplyTopic(Mockito.any(), Mockito.any());
   }

   @Test
   @DisplayName("FKPOC-869-AC4: Kafka-processmeddelande skickas till Erbjudande-topicen med handlaggningId")
   void should_send_request_message_with_erbjudande_topic_and_handlaggning_id_during_restart_process()
   {
      var handlaggningId = UUID.randomUUID();
      workflowService.restartProcess(handlaggningId, null);
      Mockito.verify(kafkaProducer, Mockito.times(1)).sendRequestMessage(eq("test"), eq(handlaggningId));
   }

   @Test
   @DisplayName("FKPOC-869-AC5: Kafka-fel vid processstart ger HandlaggningProcessStartException")
   void should_throw_handlaggning_process_start_exception_when_send_request_message_fails_during_restart_process()
   {
      var handlaggningId = UUID.randomUUID();
      Mockito.doThrow(new IllegalStateException()).when(kafkaProducer).sendRequestMessage(Mockito.any(), Mockito.any());
      assertThrows(HandlaggningProcessStartException.class, () -> workflowService.restartProcess(handlaggningId, null));
   }

   @Test
   @DisplayName("FKPOC-869-AC6: Lagringsfel av replyTo ger HandlaggningReplyTopicWriteException")
   void should_throw_handlaggning_reply_topic_write_exception_on_storage_write_failure_during_restart_process()
   {
      var handlaggningId = UUID.randomUUID();
      var replyTo = "my-replyTo-topic";
      Mockito.doThrow(new IllegalStateException()).when(workflowDataStorage)
            .storeHandlaggningReplyTopic(eq(handlaggningId), eq(replyTo));
      assertThrows(HandlaggningReplyTopicWriteException.class,
            () -> workflowService.restartProcess(handlaggningId, replyTo));
   }
}
