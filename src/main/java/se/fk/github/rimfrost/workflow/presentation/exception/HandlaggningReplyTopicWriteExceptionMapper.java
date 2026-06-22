package se.fk.github.rimfrost.workflow.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException;

@Provider
public class HandlaggningReplyTopicWriteExceptionMapper implements ExceptionMapper<HandlaggningReplyTopicWriteException>
{
   @Override
   public Response toResponse(HandlaggningReplyTopicWriteException exception)
   {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
   }
}
