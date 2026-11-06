package se.fk.github.rimfrost.workflow.integration;

public class KafkaPublishException extends RuntimeException
{
   public KafkaPublishException(Throwable cause)
   {
      super(cause);
   }
}
