package se.fk.github.rimfrost.workflow.storage.internal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import se.fk.github.rimfrost.workflow.storage.WorkflowDataStorage;
import se.fk.github.rimfrost.workflow.storage.exception.HandlaggningReplyTopicNotFoundException;
import se.fk.github.rimfrost.workflow.storage.internal.entity.HandlaggningReplyTopicEntity;
import se.fk.github.rimfrost.workflow.storage.internal.repository.HandlaggningReplyTopicRepository;

import java.util.UUID;

@ApplicationScoped
@Transactional
public class PanacheWorkflowDataStorage implements WorkflowDataStorage
{
   @Inject
   HandlaggningReplyTopicRepository handlaggningReplyTopicRepository;

   @Override
   public void storeHandlaggningReplyTopic(UUID handlaggningId, String replyTopic)
   {
      var entity = handlaggningReplyTopicRepository.findById(handlaggningId);

      if (entity == null)
      {
         entity = new HandlaggningReplyTopicEntity();
      }

      entity.setHandlaggningId(handlaggningId);
      entity.setReplyTopic(replyTopic);
      handlaggningReplyTopicRepository.persist(entity);
   }

   @Override
   public String getHandlaggningReplyTopic(UUID handlaggningId)
   {
      var entity = handlaggningReplyTopicRepository.findById(handlaggningId);

      if (entity == null)
      {
         throw new HandlaggningReplyTopicNotFoundException(
               "Handlaggning reply topic not found for handlaggning id: " + handlaggningId);
      }

      return entity.getReplyTopic();
   }

   @Override
   public void deleteHandlaggningReplyTopic(UUID handlaggningId)
   {
      handlaggningReplyTopicRepository.deleteById(handlaggningId);
   }
}
