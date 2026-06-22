package se.fk.github.rimfrost.workflow.logic.service;

import jakarta.annotation.Nullable;
import java.util.UUID;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;

public interface WorkflowService
{
   YrkandeCreateResponse createYrkande(YrkandeCreateRequest request);

   void handlaggningDone(HandlaggningResponseDTO response);

   /**
    * Restarts the process for an existing handlaggning.
    * If {@code replyTo} is non-null, it replaces any previously stored reply topic for this handlaggning.
    * The Kafka process-start message is best-effort: failures are logged and the handlaggning is returned regardless.
    *
    * @throws se.fk.github.rimfrost.workflow.logic.exception.HandlaggningNotFoundException if no handlaggning exists for {@code handlaggningId}
    * @throws se.fk.github.rimfrost.workflow.logic.exception.HandlaggningReplyTopicWriteException if persisting {@code replyTo} fails
    * @throws se.fk.github.rimfrost.workflow.logic.exception.ErbjudandeTopicReadException if the erbjudande topic lookup fails
    */
   HandlaggningDTO restartProcess(UUID handlaggningId, @Nullable String replyTo);
}
