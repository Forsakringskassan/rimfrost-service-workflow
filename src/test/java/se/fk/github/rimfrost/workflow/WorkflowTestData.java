package se.fk.github.rimfrost.workflow;

import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableRollIYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableSakfragaStallningstagandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.RollIYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.rimfrost.HandlaggningErrorInformation;
import se.fk.rimfrost.HandlaggningResponseMessageData;
import se.fk.rimfrost.HandlaggningResponseMessagePayload;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Idtyp;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostRollIYrkande;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostSakfragaStallningstagande;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class WorkflowTestData
{
   public static final String REPLY_TO = "replyTopic";

   public static YrkandeCreateRequest createYrkandeCreateRequest()
   {
      IdtypDTO idtypDTO = ImmutableIdtypDTO.builder()
            .typId(UUID.randomUUID().toString())
            .varde(UUID.randomUUID().toString())
            .build();

      RollIYrkandeCreateRequest rollIYrkandeCreateRequest = ImmutableRollIYrkandeCreateRequest.builder()
            .individ(idtypDTO)
            .yrkandeRollId(UUID.randomUUID().toString())
            .build();

      SakfragaStallningstagandeCreateRequest sakfragaStallningstagandeCreateRequest = ImmutableSakfragaStallningstagandeCreateRequest
            .builder()
            .objektTypId("ersattning")
            .data("{}")
            .build();

      return ImmutableYrkandeCreateRequest.builder()
            .ingangtypId(UUID.randomUUID().toString())
            .yrkandeFrom(OffsetDateTime.now())
            .yrkandeTom(OffsetDateTime.now())
            .handlaggningspecifikationId(UUID.randomUUID())
            .avsiktsId(UUID.randomUUID().toString())
            .replyTo(REPLY_TO)
            .rollerIYrkande(List.of(rollIYrkandeCreateRequest))
            .sakfragorStallningstaganden(List.of(sakfragaStallningstagandeCreateRequest))
            .build();
   }

   public static PostYrkandeRequest createPostYrkandeRequest()
   {
      Idtyp idtyp = new Idtyp();
      idtyp.setTypId(UUID.randomUUID().toString());
      idtyp.setVarde(UUID.randomUUID().toString());

      PostRollIYrkande rollIYrkande = new PostRollIYrkande();
      rollIYrkande.setIndivid(idtyp);
      rollIYrkande.setYrkandeRollId(UUID.randomUUID().toString());

      PostSakfragaStallningstagande sakfragaStallningstagande = new PostSakfragaStallningstagande();
      sakfragaStallningstagande.setObjektTypId("ersattning");
      sakfragaStallningstagande.setData("{}");

      PostYrkandeRequest request = new PostYrkandeRequest();
      request.ingangtypId(UUID.randomUUID().toString());
      request.yrkandeFrom(OffsetDateTime.now());
      request.yrkandeTom(OffsetDateTime.now());
      request.handlaggningspecifikationId(UUID.randomUUID());
      request.avsiktsId(UUID.randomUUID().toString());
      request.replyTo(REPLY_TO);
      request.setRollerIYrkande(List.of(rollIYrkande));
      request.setSakfragorStallningstaganden(List.of(sakfragaStallningstagande));
      return request;
   }

   public static HandlaggningDTO createHandlaggningDTO()
   {
      return ImmutableHandlaggningDTO.builder()
            .id(UUID.randomUUID())
            .yrkande(ImmutableYrkandeDTO.builder()
                  .id(UUID.randomUUID())
                  .ingangtypId(UUID.randomUUID().toString())
                  .version(1)
                  .yrkandedatum(OffsetDateTime.now())
                  .yrkandeFrom(OffsetDateTime.now())
                  .yrkandeTom(OffsetDateTime.now())
                  .yrkandestatus("YRKAT")
                  .avsikt("NY")
                  .build())
            .version(1)
            .handlaggningIdTyp(UUID.randomUUID().toString())
            .handlaggningspecifikationId(UUID.randomUUID())
            .skapadTS(OffsetDateTime.now())
            .build();
   }

   public static HandlaggningResponseDTO createHandlaggningResponseDTO()
   {
      return ImmutableHandlaggningResponseDTO.builder()
            .handlaggningId(UUID.randomUUID())
            .resultat("Ja")
            .build();
   }

   public static HandlaggningResponseMessagePayload createHandlaggningResponseMessagePayload()
   {
      HandlaggningErrorInformation handlaggningErrorInformation = new HandlaggningErrorInformation();
      handlaggningErrorInformation.setFelkod(UUID.randomUUID().toString());
      handlaggningErrorInformation.setFelmeddelande(UUID.randomUUID().toString());

      HandlaggningResponseMessageData data = new HandlaggningResponseMessageData();
      data.setHandlaggningId(UUID.randomUUID().toString());
      data.setResultat("JA");
      data.setError(handlaggningErrorInformation);

      HandlaggningResponseMessagePayload payload = new HandlaggningResponseMessagePayload();
      payload.setData(data);

      return payload;
   }

   public static HandlaggningResponseDTO createHandlaggningResponseDTO(HandlaggningResponseMessagePayload payload)
   {
      HandlaggningErrorInfoDTO handlaggningErrorInfoDTO = null;

      if (payload.getData().getError() != null)
      {
         handlaggningErrorInfoDTO = ImmutableHandlaggningErrorInfoDTO.builder()
               .felkod(payload.getData().getError().getFelkod())
               .felmeddelande(payload.getData().getError().getFelmeddelande())
               .build();
      }

      return ImmutableHandlaggningResponseDTO.builder()
            .handlaggningId(UUID.fromString(payload.getData().getHandlaggningId()))
            .resultat(payload.getData().getResultat())
            .error(handlaggningErrorInfoDTO)
            .build();
   }
}
