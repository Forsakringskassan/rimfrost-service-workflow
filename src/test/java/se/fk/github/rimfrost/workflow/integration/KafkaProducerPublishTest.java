package se.fk.github.rimfrost.workflow.integration;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KafkaProducerPublishTest
{
   private KafkaProducer producer;

   @SuppressWarnings("unchecked")
   @BeforeEach
   void setUp()
   {
      producer = new KafkaProducer();
      producer.emitter = mock(MutinyEmitter.class);
      producer.handlaggningDoneMessageEmitter = mock(MutinyEmitter.class);
      producer.publishTimeout = Duration.ofMillis(200);
   }

   @Test
   @DisplayName("FKPOC-1320: Processtart som Kafka nekar ger exception")
   void should_throw_when_kafka_rejects_handlaggning_request()
   {
      when(producer.emitter.sendMessage(any())).thenReturn(Uni.createFrom().failure(new RuntimeException("nack")));

      assertThrows(KafkaPublishException.class, () -> producer.sendRequestMessage("processTopic", UUID.randomUUID()));
   }

   @Test
   @DisplayName("FKPOC-1320: Processtart som aldrig kvitteras ger exception efter konfigurerad tid")
   void should_throw_when_handlaggning_request_is_not_acknowledged_in_time()
   {
      when(producer.emitter.sendMessage(any())).thenReturn(Uni.createFrom().nothing());

      assertThrows(KafkaPublishException.class, () -> producer.sendRequestMessage("processTopic", UUID.randomUUID()));
   }

   @Test
   @DisplayName("FKPOC-1320: Avslutningsmeddelande som Kafka nekar ger exception")
   void should_throw_when_kafka_rejects_handlaggning_done()
   {
      when(producer.handlaggningDoneMessageEmitter.sendMessage(any()))
            .thenReturn(Uni.createFrom().failure(new RuntimeException("nack")));

      assertThrows(KafkaPublishException.class, () -> producer.sendHandlaggningDone(UUID.randomUUID(), "doneTopic"));
   }

   @Test
   @DisplayName("FKPOC-1320: Avslutningsmeddelande som aldrig kvitteras ger exception efter konfigurerad tid")
   void should_throw_when_handlaggning_done_is_not_acknowledged_in_time()
   {
      when(producer.handlaggningDoneMessageEmitter.sendMessage(any())).thenReturn(Uni.createFrom().nothing());

      assertThrows(KafkaPublishException.class, () -> producer.sendHandlaggningDone(UUID.randomUUID(), "doneTopic"));
   }
}
