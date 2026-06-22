package se.fk.github.rimfrost.workflow;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.presentation.kafka.KafkaConsumer;

import static org.mockito.ArgumentMatchers.eq;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createHandlaggningResponseDTO;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createHandlaggningResponseMessagePayload;

@QuarkusTest
public class KafkaConsumerTest extends WorkflowTestBase
{
   @Inject
   KafkaConsumer kafkaConsumer;

   @InjectMock
   WorkflowService workflowService;

   @Test
   void should_not_crash_on_invalid_message() throws InterruptedException
   {
      sendKafkaMessage(handlaggningResponsesChannel, null);
      Thread.sleep(1000); // Sleep 1 sec to ensure that kafka message is processed
      Mockito.verify(workflowService, Mockito.never()).handlaggningDone(Mockito.any());
   }

   @Test
   void should_accept_handlaggning_response_message_without_error_info() throws InterruptedException
   {
      var payload = createHandlaggningResponseMessagePayload();
      payload.getData().setError(null);
      sendKafkaMessage(handlaggningResponsesChannel, payload);
      Thread.sleep(1000); // Sleep 1 sec to ensure that kafka message is processed
      Mockito.verify(workflowService, Mockito.times(1)).handlaggningDone(eq(createHandlaggningResponseDTO(payload)));
   }

   @Test
   void should_accept_handlaggning_response_message_with_error_info() throws InterruptedException
   {
      var payload = createHandlaggningResponseMessagePayload();
      sendKafkaMessage(handlaggningResponsesChannel, payload);
      Thread.sleep(1000); // Sleep 1 sec to ensure that kafka message is processed
      Mockito.verify(workflowService, Mockito.times(1)).handlaggningDone(eq(createHandlaggningResponseDTO(payload)));
   }
}
