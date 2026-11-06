package se.fk.github.rimfrost.workflow.logic.dto;

import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
public interface SakfragaStallningstagandeDTO
{
   UUID id();

   String objektTypId();

   String data();
}
