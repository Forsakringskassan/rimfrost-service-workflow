package se.fk.github.rimfrost.workflow.logic.dto;

import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
public interface RollIYrkandeDTO
{
   UUID id();

   IdtypDTO individ();

   String yrkandeRollId();

   UUID avserYrkande();
}
