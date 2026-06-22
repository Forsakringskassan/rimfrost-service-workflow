package se.fk.github.rimfrost.workflow.logic.dto;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface HandlaggningResponseDTO
{
   UUID handlaggningId();

   String resultat();

   @Nullable
   HandlaggningErrorInfoDTO error();
}
