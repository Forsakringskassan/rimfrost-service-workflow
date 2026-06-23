package se.fk.github.rimfrost.workflow.logic.exception;

/**
 * Thrown when the handlaggning adapter returns an unexpected error (non-404).
 * The original {@link se.fk.rimfrost.framework.handlaggning.exception.HandlaggningException}
 * and its {@code ErrorType} are preserved on the cause chain.
 */
public class HandlaggningAdapterException extends RuntimeException
{
   public HandlaggningAdapterException(Throwable cause)
   {
      super(cause);
   }
}
