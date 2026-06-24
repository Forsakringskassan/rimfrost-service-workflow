package se.fk.github.rimfrost.workflow;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningNotFoundException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningProcessStartException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessRequest;
import static io.restassured.RestAssured.given;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createHandlaggningDTO;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createPostYrkandeRequest;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockTestResource.class)
})
public class WorkflowControllerTest extends WorkflowTestBase
{
   @InjectMock
   WorkflowService workflowService;

   @Test
   void should_return_500_on_unexpected_exception_during_create_yrkande_request()
   {
      Mockito.when(workflowService.createYrkande(Mockito.any())).thenThrow(new RuntimeException());
      sendCreateYrkandeRequest(createPostYrkandeRequest(), 500);
   }

   @Test
   void should_return_500_on_erbjudande_topic_read_exception_during_create_yrkande_request()
   {
      Mockito.when(workflowService.createYrkande(Mockito.any()))
            .thenThrow(new ErbjudandeTopicReadException(new RuntimeException()));
      sendCreateYrkandeRequest(createPostYrkandeRequest(), 500);
   }

   @Test
   void should_return_500_on_handlaggning_reply_topic_write_exception_during_create_yrkande_request()
   {
      Mockito.when(workflowService.createYrkande(Mockito.any()))
            .thenThrow(new HandlaggningReplyTopicWriteException(new RuntimeException()));
      sendCreateYrkandeRequest(createPostYrkandeRequest(), 500);
   }

   @Test
   void should_return_500_on_handlaggning_update_exception_during_create_yrkande_request()
   {
      Mockito.when(workflowService.createYrkande(Mockito.any()))
            .thenThrow(new HandlaggningReplyTopicWriteException(new RuntimeException()));
      sendCreateYrkandeRequest(createPostYrkandeRequest(), 500);
   }

   @Test
   @DisplayName("FKPOC-869-AC1: POST /handlaggning/{id}/process returnerar 200 med handläggning vid lyckat anrop")
   void should_return_200_with_handlaggning_on_restart_process()
   {
      Mockito.when(workflowService.restartProcess(Mockito.any(), Mockito.any())).thenReturn(createHandlaggningDTO());
      given().contentType(ContentType.JSON)
            .body(new PostHandlaggningProcessRequest(WorkflowTestData.REPLY_TO))
            .post("/handlaggning/{id}/process", UUID.randomUUID())
            .then()
            .statusCode(200);
   }

   @Test
   @DisplayName("FKPOC-869-AC2: POST /handlaggning/{id}/process returnerar 404 när handläggning saknas")
   void should_return_404_when_handlaggning_not_found_on_restart_process()
   {
      Mockito.when(workflowService.restartProcess(Mockito.any(), Mockito.any()))
            .thenThrow(new HandlaggningNotFoundException(new RuntimeException()));
      given().contentType(ContentType.JSON)
            .body(new PostHandlaggningProcessRequest(WorkflowTestData.REPLY_TO))
            .post("/handlaggning/{id}/process", UUID.randomUUID())
            .then()
            .statusCode(404);
   }

   @Test
   @DisplayName("FKPOC-869-AC5: POST /handlaggning/{id}/process returnerar 500 när Kafka-processstart misslyckas")
   void should_return_500_when_process_start_fails_on_restart_process()
   {
      Mockito.when(workflowService.restartProcess(Mockito.any(), Mockito.any()))
            .thenThrow(new HandlaggningProcessStartException(new RuntimeException()));
      given().contentType(ContentType.JSON)
            .body(new PostHandlaggningProcessRequest(WorkflowTestData.REPLY_TO))
            .post("/handlaggning/{id}/process", UUID.randomUUID())
            .then()
            .statusCode(500);
   }

   @Test
   @DisplayName("FKPOC-869-AC6: POST /handlaggning/{id}/process returnerar 500 när lagring av replyTo misslyckas")
   void should_return_500_when_reply_topic_write_fails_on_restart_process()
   {
      Mockito.when(workflowService.restartProcess(Mockito.any(), Mockito.any()))
            .thenThrow(new HandlaggningReplyTopicWriteException(new RuntimeException()));
      given().contentType(ContentType.JSON)
            .body(new PostHandlaggningProcessRequest(WorkflowTestData.REPLY_TO))
            .post("/handlaggning/{id}/process", UUID.randomUUID())
            .then()
            .statusCode(500);
   }

   @Test
   @DisplayName("FKPOC-874: POST /handlaggning/{id}/process returnerar 400 när replyTo saknas")
   void should_return_400_when_reply_to_is_missing_on_restart_process()
   {
      given().contentType(ContentType.JSON)
            .body(new PostHandlaggningProcessRequest())
            .post("/handlaggning/{id}/process", UUID.randomUUID())
            .then()
            .statusCode(400);
   }

   @Test
   @DisplayName("FKPOC-874: POST /yrkande returnerar 400 när replyTo saknas")
   void should_return_400_when_reply_to_is_missing_on_create_yrkande()
   {
      PostYrkandeRequest request = createPostYrkandeRequest();
      request.setReplyTo(null);
      sendCreateYrkandeRequest(request, 400);
   }
}
