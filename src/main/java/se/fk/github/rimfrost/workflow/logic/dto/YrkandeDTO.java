package se.fk.github.rimfrost.workflow.logic.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.immutables.value.Value;

@Value.Immutable
public interface YrkandeDTO
{
   UUID id();

   String ingangtypId();

   Integer version();

   OffsetDateTime yrkandedatum();

   OffsetDateTime yrkandeFrom();

   OffsetDateTime yrkandeTom();

   String yrkandestatus();

   String avsikt();

   @Value.Default
   default List<BeslutDTO> beslut()
   {
      return List.of();
   }

   @Value.Default
   default List<RollIYrkandeDTO> rollerIYrkande()
   {
      return List.of();
   }

   @Value.Default
   default List<SakfragaStallningstagandeDTO> sakfragorStallningstaganden()
   {
      return List.of();
   }
}
