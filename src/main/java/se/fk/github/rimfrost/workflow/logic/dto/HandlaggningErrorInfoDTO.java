package se.fk.github.rimfrost.workflow.logic.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface HandlaggningErrorInfoDTO
{
   String felkod();

   String felmeddelande();
}
