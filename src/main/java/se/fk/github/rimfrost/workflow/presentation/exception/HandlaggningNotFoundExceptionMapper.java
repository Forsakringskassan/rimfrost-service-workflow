package se.fk.github.rimfrost.workflow.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import se.fk.github.rimfrost.workflow.logic.exception.HandlaggningNotFoundException;

/**
 * Maps {@link HandlaggningNotFoundException} to HTTP 404.
 */
@Provider
public class HandlaggningNotFoundExceptionMapper implements ExceptionMapper<HandlaggningNotFoundException>
{
   @Override
   public Response toResponse(HandlaggningNotFoundException exception)
   {
      return Response.status(Response.Status.NOT_FOUND).build();
   }
}
