package se.fk.github.rimfrost.workflow.logic.dto;

import org.immutables.value.Value;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface BeslutDTO
{
   UUID id();

   int version();

   OffsetDateTime datum();

   IdtypDTO beslutsfattare();

   List<BeslutsradDTO> beslutsrader();
}
