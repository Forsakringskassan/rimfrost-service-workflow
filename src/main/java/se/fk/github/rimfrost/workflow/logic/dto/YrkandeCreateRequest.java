package se.fk.github.rimfrost.workflow.logic.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.immutables.value.Value;

@Value.Immutable
public interface YrkandeCreateRequest
{

   String ingangtypId();

   OffsetDateTime yrkandeFrom();

   OffsetDateTime yrkandeTom();

   UUID handlaggningspecifikationId();

   String avsiktsId();

   String replyTo();

   List<RollIYrkandeCreateRequest> rollerIYrkande();

   List<SakfragaStallningstagandeCreateRequest> sakfragorStallningstaganden();

}
