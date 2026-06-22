package se.fk.github.rimfrost.workflow.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningUpdateException;

@Provider
public class HandlaggningUpdateExceptionMapper implements ExceptionMapper<HandlaggningUpdateException>
{
   @Override
   public Response toResponse(HandlaggningUpdateException exception)
   {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
   }
}
