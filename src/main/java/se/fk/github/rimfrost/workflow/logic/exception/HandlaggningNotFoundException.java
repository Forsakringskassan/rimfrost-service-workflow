package se.fk.github.rimfrost.workflow.logic.exception;

/**
 * Thrown when a handlaggning cannot be found for a given ID.
 * Mapped to HTTP 404 by {@link se.fk.github.rimfrost.workflow.presentation.exception.HandlaggningNotFoundExceptionMapper}.
 */
public class HandlaggningNotFoundException extends RuntimeException
{
   public HandlaggningNotFoundException(Throwable cause)
   {
      super(cause);
   }
}
