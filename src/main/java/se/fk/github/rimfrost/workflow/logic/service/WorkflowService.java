package se.fk.github.rimfrost.workflow.logic.service;

import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;

public interface WorkflowService
{
   YrkandeCreateResponse createYrkande(YrkandeCreateRequest request);

   void handlaggningDone(HandlaggningResponseDTO response);
}
