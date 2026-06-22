package se.fk.github.rimfrost.workflow.storage;

import java.util.UUID;

public interface WorkflowDataStorage
{
   void storeHandlaggningReplyTopic(UUID handlaggningId, String replyTopic);

   String getHandlaggningReplyTopic(UUID handlaggningId);

   void deleteHandlaggningReplyTopic(UUID handlaggningId);
}
