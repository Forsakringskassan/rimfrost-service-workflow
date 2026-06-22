package se.fk.github.rimfrost.workflow;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import se.fk.rimfrost.HandlaggningDoneMessage;
import se.fk.rimfrost.HandlaggningRequestMessagePayload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createHandlaggningResponseMessagePayload;
import static se.fk.github.rimfrost.workflow.WorkflowTestData.createPostYrkandeRequest;

@QuarkusTest
public class SequenceTest extends WorkflowTestBase
{
   @Test
   void workflow_smoke_test()
   {
      // Create yrkande & trigger handlaggning process
      var createResponse = sendCreateYrkandeRequest(createPostYrkandeRequest());
      assertNotNull(createResponse);

      assertNotNull(createResponse.getHandlaggning());
      var handlaggningId = createResponse.getHandlaggning().getId();

      // Wait for handlaggning process message
      var handlaggningRequestMsgs = waitForMessages(handlaggningRequestsChannel);
      assertEquals(1, handlaggningRequestMsgs.size());

      // Verify that the correct handlaggning was requested for the correct id
      var handlaggningRequestMsg = handlaggningRequestMsgs.getFirst();
      assertNotNull(handlaggningRequestMsg);
      assertInstanceOf(HandlaggningRequestMessagePayload.class, handlaggningRequestMsg.getPayload());

      var handlaggningRequest = (HandlaggningRequestMessagePayload) handlaggningRequestMsg.getPayload();
      assertEquals(handlaggningId.toString(), handlaggningRequest.getData().getHandlaggningId());

      // Send handlaggning response
      var handlaggningResponse = createHandlaggningResponseMessagePayload();
      handlaggningResponse.getData().setHandlaggningId(handlaggningId.toString());
      handlaggningResponse.getData().setError(null);
      sendKafkaMessage(handlaggningResponsesChannel, handlaggningResponse);

      // Wait for handlaggning done msg
      var handlaggningDoneMsgs = waitForMessages(handlaggningDoneChannel);
      assertEquals(1, handlaggningDoneMsgs.size());

      // Verify that handlaggning done was sent for the correct handlaggning id
      var handlaggningDoneMsg = handlaggningDoneMsgs.getFirst();
      assertNotNull(handlaggningDoneMsg);
      assertInstanceOf(HandlaggningDoneMessage.class, handlaggningDoneMsg.getPayload());

      var handlaggningDone = (HandlaggningDoneMessage) handlaggningDoneMsg.getPayload();
      assertEquals(handlaggningId.toString(), handlaggningDone.getHandlaggningId());
   }
}
