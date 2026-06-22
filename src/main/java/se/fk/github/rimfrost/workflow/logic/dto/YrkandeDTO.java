package se.fk.github.rimfrost.workflow.logic.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface YrkandeDTO
{
   UUID id();

   String erbjudandeId();

   Integer version();

   OffsetDateTime yrkandedatum();

   OffsetDateTime yrkandeFrom();

   OffsetDateTime yrkandeTom();

   String yrkandestatus();

   String avsikt();

   @Nullable
   BeslutDTO beslut();

   @Value.Default
   default List<IndividYrkandeRollDTO> individYrkandeRoll()
   {
      return List.of();
   }

   @Value.Default
   default List<ProduceratResultatDTO> produceradeResultat()
   {
      return List.of();
   }
}
