package se.fk.github.rimfrost.workflow.logic.dto;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface BeslutsradDTO
{
   UUID id();

   int version();

   String beslutsTyp();

   String beslutsUtfall();

   String avslutsTyp();

   List<ProduceratResultatRefDTO> produceratResultatRefs();
}
