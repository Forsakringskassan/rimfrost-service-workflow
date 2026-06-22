package se.fk.github.rimfrost.workflow;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import se.fk.github.rimfrost.workflow.storage.WorkflowDataStorage;
import se.fk.github.rimfrost.workflow.storage.exception.HandlaggningReplyTopicNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class WorkflowDataStorageTest extends WorkflowTestBase
{
   @Inject
   WorkflowDataStorage workflowDataStorage;

   @Test
   void should_persist_reply_topic_on_write()
   {
      var handlaggningId = UUID.randomUUID();
      var handlaggningReplyTopic = UUID.randomUUID().toString();

      workflowDataStorage.storeHandlaggningReplyTopic(handlaggningId, handlaggningReplyTopic);
      var storedHandlaggningReplyTopic = workflowDataStorage.getHandlaggningReplyTopic(handlaggningId);
      assertEquals(handlaggningReplyTopic, storedHandlaggningReplyTopic);
   }

   @Test
   void should_overwrite_reply_topic_if_exists()
   {
      var handlaggningId = UUID.randomUUID();
      var handlaggningReplyTopic1 = UUID.randomUUID().toString();
      var handlaggningReplyTopic2 = UUID.randomUUID().toString();

      workflowDataStorage.storeHandlaggningReplyTopic(handlaggningId, handlaggningReplyTopic1);
      workflowDataStorage.storeHandlaggningReplyTopic(handlaggningId, handlaggningReplyTopic2);
      var storedHandlaggningReplyTopic = workflowDataStorage.getHandlaggningReplyTopic(handlaggningId);
      assertEquals(handlaggningReplyTopic2, storedHandlaggningReplyTopic);
   }

   @Test
   void should_throw_exception_if_handlaggning_reply_topic_not_found()
   {
      assertThrows(HandlaggningReplyTopicNotFoundException.class,
            () -> workflowDataStorage.getHandlaggningReplyTopic(UUID.randomUUID()));
   }

   @Test
   void should_remove_reply_topic_on_delete()
   {
      var handlaggningId = UUID.randomUUID();
      var handlaggningReplyTopic = UUID.randomUUID().toString();

      workflowDataStorage.storeHandlaggningReplyTopic(handlaggningId, handlaggningReplyTopic);
      workflowDataStorage.deleteHandlaggningReplyTopic(handlaggningId);
      assertThrows(HandlaggningReplyTopicNotFoundException.class,
            () -> workflowDataStorage.getHandlaggningReplyTopic(handlaggningId));
   }
}
