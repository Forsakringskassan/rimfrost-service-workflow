package se.fk.github.rimfrost.workflow;

import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableProduceratResultatCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ProduceratResultatCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.rimfrost.HandlaggningErrorInformation;
import se.fk.rimfrost.HandlaggningResponseMessageData;
import se.fk.rimfrost.HandlaggningResponseMessagePayload;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Idtyp;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.IndividYrkandeRoll;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.ProduceratResultat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class WorkflowTestData
{
   public static YrkandeCreateRequest createYrkandeCreateRequest()
   {
      IdtypDTO idtypDTO = ImmutableIdtypDTO.builder()
            .typId(UUID.randomUUID().toString())
            .varde(UUID.randomUUID().toString())
            .build();

      IndividYrkandeRollDTO individYrkandeRollDTO = ImmutableIndividYrkandeRollDTO.builder()
            .individ(idtypDTO)
            .yrkandeRollId(UUID.randomUUID().toString())
            .build();

      ProduceratResultatCreateRequest produceratResultatCreateRequest = ImmutableProduceratResultatCreateRequest.builder()
            .franOchMed(OffsetDateTime.now())
            .tillOchMed(OffsetDateTime.now())
            .typ("ersattning")
            .data("{}")
            .build();

      return ImmutableYrkandeCreateRequest.builder()
            .erbjudandeId(UUID.randomUUID().toString())
            .yrkandeFrom(OffsetDateTime.now())
            .yrkandeTom(OffsetDateTime.now())
            .handlaggningspecifikationId(UUID.randomUUID())
            .avsiktsId(UUID.randomUUID().toString())
            .replyTo("replyTopic")
            .individYrkandeRoller(List.of(individYrkandeRollDTO))
            .produceradeResultat(List.of(produceratResultatCreateRequest))
            .build();
   }

   public static PostYrkandeRequest createPostYrkandeRequest()
   {
      Idtyp idtyp = new Idtyp();
      idtyp.setTypId(UUID.randomUUID().toString());
      idtyp.setVarde(UUID.randomUUID().toString());

      IndividYrkandeRoll individYrkandeRoll = new IndividYrkandeRoll();
      individYrkandeRoll.setIndivid(idtyp);
      individYrkandeRoll.setYrkandeRollId(UUID.randomUUID().toString());

      ProduceratResultat produceratResultat = new ProduceratResultat();
      produceratResultat.setId(UUID.randomUUID());
      produceratResultat.setFrom(OffsetDateTime.now());
      produceratResultat.setTom(OffsetDateTime.now());
      produceratResultat.setVersion(1);
      produceratResultat.setTyp("ersattning");
      produceratResultat.setData("{}");

      PostYrkandeRequest request = new PostYrkandeRequest();
      request.erbjudandeId(UUID.randomUUID().toString());
      request.yrkandeFrom(OffsetDateTime.now());
      request.yrkandeTom(OffsetDateTime.now());
      request.handlaggningspecifikationId(UUID.randomUUID());
      request.avsiktsId(UUID.randomUUID().toString());
      request.replyTo("replyTopic");
      request.setIndividYrkandeRoller(List.of(individYrkandeRoll));
      request.setProduceradeResultat(List.of(produceratResultat));
      return request;
   }

   public static HandlaggningDTO createHandlaggningDTO()
   {
      return ImmutableHandlaggningDTO.builder()
            .id(UUID.randomUUID())
            .yrkande(ImmutableYrkandeDTO.builder()
                  .id(UUID.randomUUID())
                  .erbjudandeId(UUID.randomUUID().toString())
                  .version(1)
                  .yrkandedatum(OffsetDateTime.now())
                  .yrkandeFrom(OffsetDateTime.now())
                  .yrkandeTom(OffsetDateTime.now())
                  .yrkandestatus("YRKAT")
                  .avsikt("NY")
                  .build())
            .version(1)
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
