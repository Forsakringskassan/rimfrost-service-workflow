package se.fk.github.rimfrost.workflow.logic.dto;

import java.time.OffsetDateTime;

import org.immutables.value.Value;

@Value.Immutable
public interface ProduceratResultatCreateRequest
{

   OffsetDateTime franOchMed();

   OffsetDateTime tillOchMed();

   String typ();

   String data();

}
