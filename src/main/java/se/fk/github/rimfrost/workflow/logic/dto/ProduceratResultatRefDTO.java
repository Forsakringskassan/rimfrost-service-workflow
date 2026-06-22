package se.fk.github.rimfrost.workflow.logic.dto;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface ProduceratResultatRefDTO
{
   UUID id();

   int version();
}
