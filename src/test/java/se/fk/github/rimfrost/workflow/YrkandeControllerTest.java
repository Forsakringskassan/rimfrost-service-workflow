package se.fk.github.rimfrost.workflow;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;

import static se.fk.github.rimfrost.workflow.WorkflowTestData.createPostYrkandeRequest;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockTestResource.class)
})
public class YrkandeControllerTest extends WorkflowTestBase
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
}
