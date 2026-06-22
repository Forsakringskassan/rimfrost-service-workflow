package se.fk.github.rimfrost.workflow.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException;

@Provider
public class ErbjudandeTopicReadExceptionMapper implements ExceptionMapper<ErbjudandeTopicReadException>
{
   @Override
   public Response toResponse(ErbjudandeTopicReadException exception)
   {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
   }
}
