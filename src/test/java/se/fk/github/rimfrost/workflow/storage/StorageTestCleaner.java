package se.fk.github.rimfrost.workflow.storage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StorageTestCleaner
{
   @Inject
   EntityManager em;

   @Transactional
   public void clearAll()
   {
      em.createQuery("DELETE FROM HandlaggningReplyTopicEntity").executeUpdate();
   }
}
