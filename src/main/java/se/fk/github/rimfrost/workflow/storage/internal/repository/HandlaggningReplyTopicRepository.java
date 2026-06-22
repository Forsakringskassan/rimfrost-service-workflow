package se.fk.github.rimfrost.workflow.storage.internal.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import se.fk.github.rimfrost.workflow.storage.internal.entity.HandlaggningReplyTopicEntity;

import java.util.UUID;

@ApplicationScoped
public class HandlaggningReplyTopicRepository implements PanacheRepositoryBase<HandlaggningReplyTopicEntity, UUID>
{
}
