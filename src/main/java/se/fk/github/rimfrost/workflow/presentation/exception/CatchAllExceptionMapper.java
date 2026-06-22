package se.fk.github.rimfrost.workflow.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CatchAllExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Exception>
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CatchAllExceptionMapper.class);

   @Override
   public Response toResponse(Exception exception)
   {
      LOGGER.error("Request terminated due to unexpected exception", exception);

      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
   }
}
