package se.fk.github.rimfrost.workflow.presentation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.UUID;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.presentation.util.PresentationMapper;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.WorkflowControllerApi;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessResponse;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeResponse;

/**
 * REST controller implementing the workflow API endpoints for yrkande and handlaggning.
 */
@Path("")
@ApplicationScoped
public class WorkflowController implements WorkflowControllerApi
{
   @Inject
   WorkflowService workflowService;

   @Inject
   PresentationMapper mapper;

   @Override
   @POST
   @Path("/yrkande")
   @Consumes(
   {
         "application/json"
   })
   @Produces(
   {
         "application/json"
   })
   public PostYrkandeResponse postYrkande(PostYrkandeRequest postYrkandeRequest)
   {
      YrkandeCreateRequest yrkandeCreateRequest = mapper.toYrkandeCreateRequest(postYrkandeRequest);
      YrkandeCreateResponse yrkandeCreateResponse = workflowService.createYrkande(yrkandeCreateRequest);
      return mapper.toPostYrkandeResponse(yrkandeCreateResponse);
   }

   @Override
   public PostHandlaggningProcessResponse postHandlaggningProcess(UUID handlaggningId,
         PostHandlaggningProcessRequest postHandlaggningProcessRequest)
   {
      HandlaggningDTO handlaggningDTO = workflowService.restartProcess(
            handlaggningId, postHandlaggningProcessRequest.getReplyTo());
      return mapper.toPostHandlaggningProcessResponse(handlaggningDTO);
   }
}
