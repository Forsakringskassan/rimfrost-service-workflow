package se.fk.github.rimfrost.workflow.presentation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.service.WorkflowService;
import se.fk.github.rimfrost.workflow.presentation.util.PresentationMapper;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.YrkandeControllerApi;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeResponse;

@Path("")
@ApplicationScoped
public class YrkandeController implements YrkandeControllerApi
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
}
