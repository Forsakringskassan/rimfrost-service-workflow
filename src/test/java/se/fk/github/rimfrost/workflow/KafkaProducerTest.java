package se.fk.github.rimfrost.workflow;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.fk.github.rimfrost.workflow.integration.KafkaProducer;
import se.fk.rimfrost.HandlaggningDoneMessage;
import se.fk.rimfrost.HandlaggningRequestMessagePayload;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class KafkaProducerTest extends WorkflowTestBase
{
   @Inject
   KafkaProducer kafkaProducer;

   @Test
   void should_send_handlaggning_request_with_provided_id_and_topic()
   {
      var handlaggningId = UUID.randomUUID();
      var expectedTopic = "processTopic";
      kafkaProducer.sendRequestMessage(expectedTopic, handlaggningId);
      var messages = waitForMessages(handlaggningRequestsChannel);

      assertEquals(1, messages.size());
      var message = messages.getFirst();

      var metadata = message.getMetadata(OutgoingKafkaRecordMetadata.class).orElseThrow();
      assertEquals(expectedTopic, metadata.getTopic());

      assertInstanceOf(HandlaggningRequestMessagePayload.class, message.getPayload());
      var payload = (HandlaggningRequestMessagePayload) message.getPayload();
      assertNotNull(payload.getData());
      assertEquals(handlaggningId.toString(), payload.getData().getHandlaggningId());
   }

   @Test
   void should_send_handlaggning_done_with_provided_id_topic()
   {
      var handlaggningId = UUID.randomUUID();
      var expectedTopic = "doneTopic";
      kafkaProducer.sendHandlaggningDone(handlaggningId, expectedTopic);
      var messages = waitForMessages(handlaggningDoneChannel);

      assertEquals(1, messages.size());
      var message = messages.getFirst();

      var metadata = message.getMetadata(OutgoingKafkaRecordMetadata.class).orElseThrow();
      assertEquals(expectedTopic, metadata.getTopic());

      assertInstanceOf(HandlaggningDoneMessage.class, message.getPayload());
      var payload = (HandlaggningDoneMessage) message.getPayload();
      assertEquals(handlaggningId.toString(), payload.getHandlaggningId());
   }
}
