package se.fk.github.rimfrost.workflow.presentation.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import se.fk.rimfrost.HandlaggningResponseMessagePayload;

public class HandlaggningResponseMessagePayloadDeserializer
      extends ObjectMapperDeserializer<HandlaggningResponseMessagePayload>
{

   public HandlaggningResponseMessagePayloadDeserializer()
   {
      super(HandlaggningResponseMessagePayload.class);
   }

}
