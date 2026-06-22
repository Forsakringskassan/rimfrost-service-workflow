package se.fk.github.rimfrost.workflow.storage.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "handlaggning_reply_topic")
public class HandlaggningReplyTopicEntity
{
   @Id
   private UUID handlaggningId;

   @Column(nullable = false)
   private String replyTopic;

   @Column(nullable = false, updatable = false)
   private Instant createdAt;

   public UUID getHandlaggningId()
   {
      return handlaggningId;
   }

   public void setHandlaggningId(UUID handlaggningId)
   {
      this.handlaggningId = handlaggningId;
   }

   public String getReplyTopic()
   {
      return replyTopic;
   }

   public void setReplyTopic(String replyTopic)
   {
      this.replyTopic = replyTopic;
   }

   @PrePersist
   void onCreate()
   {
      createdAt = Instant.now();
   }
}
