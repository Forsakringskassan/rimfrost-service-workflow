package se.fk.github.rimfrost.workflow.logic.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.immutables.value.Value;

import jakarta.annotation.Nullable;

@Value.Immutable
public interface ProduceratResultatDTO
{
   UUID id();

   Integer version();

   OffsetDateTime franOchMed();

   OffsetDateTime tillOchMed();

   String yrkandestatus();

   @Nullable
   String avslagsanledning();

   String typ();

   String data();

}
