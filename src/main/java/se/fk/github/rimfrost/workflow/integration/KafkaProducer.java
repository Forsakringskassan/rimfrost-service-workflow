package se.fk.github.rimfrost.workflow.integration;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;
import se.fk.rimfrost.*;

@ApplicationScoped
public class KafkaProducer
{

   @Channel("handlaggning-requests")
   Emitter<HandlaggningRequestMessagePayload> emitter;

   public void sendRequestMessage(String topic, UUID handlaggningId)
   {
      var data = new HandlaggningRequestMessageData();
      data.setHandlaggningId(handlaggningId.toString());

      var payload = new HandlaggningRequestMessagePayload();
      payload.setType(topic);
      payload.setSource("/service/workflow");
      payload.setTime(OffsetDateTime.now());
      payload.setSpecversion(SpecVersion.V1);
      payload.setKogitoproctype("BPMN");
      payload.setData(data);

      var metadata = OutgoingKafkaRecordMetadata.builder()
            .withTopic(topic)
            .build();
      var message = Message.of(payload).addMetadata(metadata);

      emitter.send(message);
   }

   @Channel("handlaggning-done")
   Emitter<HandlaggningDoneMessage> handlaggningDoneMessageEmitter;

   public void sendHandlaggningDone(UUID handlaggningId, String topic)
   {
      var payload = new HandlaggningDoneMessage();
      payload.setHandlaggningId(handlaggningId.toString());

      var metadata = OutgoingKafkaRecordMetadata.builder()
            .withTopic(topic)
            .build();
      var message = Message.of(payload).addMetadata(metadata);

      handlaggningDoneMessageEmitter.send(message);
   }
}
