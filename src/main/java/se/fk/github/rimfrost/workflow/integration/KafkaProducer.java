package se.fk.github.rimfrost.workflow.integration;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;
import se.fk.rimfrost.*;

@ApplicationScoped
public class KafkaProducer
{

   @ConfigProperty(name = "rimfrost.workflow.publish-timeout", defaultValue = "10s")
   Duration publishTimeout;

   @Channel("handlaggning-requests")
   MutinyEmitter<HandlaggningRequestMessagePayload> emitter;

   public void sendRequestMessage(String topic, UUID handlaggningId)
   {
      var data = new HandlaggningRequestMessageData();
      data.setHandlaggningId(handlaggningId.toString());

      var payload = new HandlaggningRequestMessagePayload();
      payload.setId(UUID.randomUUID().toString());
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

      send(emitter, message);
   }

   @Channel("handlaggning-done")
   MutinyEmitter<HandlaggningDoneMessage> handlaggningDoneMessageEmitter;

   public void sendHandlaggningDone(UUID handlaggningId, String topic)
   {
      var payload = new HandlaggningDoneMessage();
      payload.setHandlaggningId(handlaggningId.toString());

      var metadata = OutgoingKafkaRecordMetadata.builder()
            .withTopic(topic)
            .build();
      var message = Message.of(payload).addMetadata(metadata);

      send(handlaggningDoneMessageEmitter, message);
   }

   private <T> void send(MutinyEmitter<T> emitter, Message<T> message)
   {
      try
      {
         emitter.sendMessage(message).await().atMost(publishTimeout);
      }
      catch (RuntimeException e)
      {
         throw new KafkaPublishException(e);
      }
   }
}
