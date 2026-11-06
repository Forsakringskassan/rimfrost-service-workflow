package se.fk.github.rimfrost.workflow.presentation.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.enterprise.context.ApplicationScoped;
import se.fk.github.rimfrost.workflow.logic.dto.BeslutDTO;
import se.fk.github.rimfrost.workflow.logic.dto.BeslutsradDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningErrorInfoDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningResponseDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableRollIYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableSakfragaStallningstagandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.RollIYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeRefDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeDTO;
import se.fk.rimfrost.HandlaggningErrorInformation;
import se.fk.rimfrost.HandlaggningResponseMessageData;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Beslut;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Beslutsrad;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Handlaggning;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Idtyp;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessResponse;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeResponse;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.RollIYrkande;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.SakfragaStallningstagandeContainer;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.SakfragaStallningstagandeRef;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Yrkande;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class PresentationMapper
{
   public YrkandeCreateRequest toYrkandeCreateRequest(PostYrkandeRequest postYrkandeRequest)
   {
      var rollerIYrkande = postYrkandeRequest.getRollerIYrkande().stream()
            .map(e -> ImmutableRollIYrkandeCreateRequest.builder()
                  .individ(toIdtypDTO(e.getIndivid()))
                  .yrkandeRollId(e.getYrkandeRollId())
                  .build())
            .toList();

      var sakfragorStallningstaganden = postYrkandeRequest.getSakfragorStallningstaganden().stream()
            .map(e -> ImmutableSakfragaStallningstagandeCreateRequest.builder()
                  .objektTypId(e.getObjektTypId())
                  .data(e.getData())
                  .build())
            .toList();

      return ImmutableYrkandeCreateRequest.builder()
            .ingangtypId(postYrkandeRequest.getIngangtypId())
            .yrkandeFrom(postYrkandeRequest.getYrkandeFrom())
            .yrkandeTom(postYrkandeRequest.getYrkandeTom())
            .handlaggningspecifikationId(postYrkandeRequest.getHandlaggningspecifikationId())
            .avsiktsId(postYrkandeRequest.getAvsiktsId())
            .replyTo(postYrkandeRequest.getReplyTo())
            .rollerIYrkande(rollerIYrkande)
            .sakfragorStallningstaganden(sakfragorStallningstaganden)
            .build();
   }

   /**
    * Maps a {@link HandlaggningDTO} to a {@link PostHandlaggningProcessResponse}.
    */
   public PostHandlaggningProcessResponse toPostHandlaggningProcessResponse(HandlaggningDTO handlaggningDTO)
   {
      PostHandlaggningProcessResponse response = new PostHandlaggningProcessResponse();
      response.setHandlaggning(toHandlaggning(handlaggningDTO));
      return response;
   }

   public PostYrkandeResponse toPostYrkandeResponse(YrkandeCreateResponse yrkandeCreateResponse)
   {
      PostYrkandeResponse postYrkandeResponse = new PostYrkandeResponse();
      postYrkandeResponse.setHandlaggning(toHandlaggning(yrkandeCreateResponse.handlaggning()));
      return postYrkandeResponse;
   }

   @SuppressFBWarnings(value = "NP_NULL_PARAM_DEREF", justification = "False positive for errorInfo null check")
   public HandlaggningResponseDTO toHandlaggningResponseDTO(HandlaggningResponseMessageData handlaggningResponse)
   {
      var data = Objects.requireNonNull(handlaggningResponse);

      HandlaggningErrorInfoDTO handlaggningErrorInfoDTO = null;
      HandlaggningErrorInformation errorInfo = data.getError();

      if (errorInfo != null)
      {
         handlaggningErrorInfoDTO = ImmutableHandlaggningErrorInfoDTO.builder()
               .felkod(errorInfo.getFelkod())
               .felmeddelande(errorInfo.getFelmeddelande())
               .build();
      }

      return ImmutableHandlaggningResponseDTO.builder()
            .handlaggningId(UUID.fromString(data.getHandlaggningId()))
            .resultat(data.getResultat())
            .error(handlaggningErrorInfoDTO)
            .build();
   }

   private IdtypDTO toIdtypDTO(Idtyp idtyp)
   {
      if (idtyp == null)
      {
         return null;
      }

      return ImmutableIdtypDTO.builder()
            .typId(idtyp.getTypId())
            .varde(idtyp.getVarde())
            .build();
   }

   private Idtyp toIdtyp(IdtypDTO idtypDTO)
   {
      if (idtypDTO == null)
      {
         return null;
      }

      Idtyp idtyp = new Idtyp();
      idtyp.setTypId(idtypDTO.typId());
      idtyp.setVarde(idtypDTO.varde());

      return idtyp;
   }

   private Yrkande toYrkande(YrkandeDTO yrkandeDTO)
   {
      Yrkande yrkande = new Yrkande();
      yrkande.setId(yrkandeDTO.id());
      yrkande.setIngangtypId(yrkandeDTO.ingangtypId());
      yrkande.setVersion(yrkandeDTO.version());
      yrkande.setYrkandedatum(yrkandeDTO.yrkandedatum());
      yrkande.setYrkandeFrom(yrkandeDTO.yrkandeFrom());
      yrkande.setYrkandeTom(yrkandeDTO.yrkandeTom());
      yrkande.setYrkandestatus(yrkandeDTO.yrkandestatus());
      yrkande.setAvsikt(yrkandeDTO.avsikt());
      yrkande.setRollerIYrkande(
            yrkandeDTO.rollerIYrkande()
                  .stream()
                  .map(this::toRollIYrkande)
                  .toList());
      yrkande.setSakfragorStallningstaganden(
            yrkandeDTO.sakfragorStallningstaganden()
                  .stream()
                  .map(this::toSakfragaStallningstagande)
                  .toList());
      yrkande.setBeslut(
            yrkandeDTO.beslut()
                  .stream()
                  .map(this::toBeslut)
                  .toList());
      return yrkande;
   }

   private RollIYrkande toRollIYrkande(RollIYrkandeDTO rollIYrkandeDTO)
   {
      var rollIYrkande = new RollIYrkande();
      rollIYrkande.setId(rollIYrkandeDTO.id());
      rollIYrkande.setIndivid(toIdtyp(rollIYrkandeDTO.individ()));
      rollIYrkande.setYrkandeRollId(rollIYrkandeDTO.yrkandeRollId());
      rollIYrkande.setAvserYrkande(rollIYrkandeDTO.avserYrkande());
      return rollIYrkande;
   }

   private SakfragaStallningstagandeContainer toSakfragaStallningstagande(
         SakfragaStallningstagandeDTO sakfragaStallningstagandeDTO)
   {
      var sakfragaStallningstagande = new SakfragaStallningstagandeContainer();
      sakfragaStallningstagande.setId(sakfragaStallningstagandeDTO.id());
      sakfragaStallningstagande.setObjektTypId(sakfragaStallningstagandeDTO.objektTypId());
      sakfragaStallningstagande.setData(sakfragaStallningstagandeDTO.data());
      return sakfragaStallningstagande;
   }

   private Beslut toBeslut(BeslutDTO beslutDTO)
   {
      Beslut beslut = new Beslut();
      beslut.setId(beslutDTO.id());
      beslut.setVersion(beslutDTO.version());
      beslut.setDatum(beslutDTO.datum());
      beslut.setBeslutsfattare(toIdtyp(beslutDTO.beslutsfattare()));
      beslut.setBeslutsrader(beslutDTO.beslutsrader().stream().map(this::toBeslutsrad).toList());

      return beslut;
   }

   private Beslutsrad toBeslutsrad(BeslutsradDTO beslutsradDTO)
   {
      Beslutsrad beslutsrad = new Beslutsrad();
      beslutsrad.setId(beslutsradDTO.id());
      beslutsrad.setVersion(beslutsradDTO.version());
      beslutsrad.setAvslutsTyp(beslutsradDTO.avslutsTyp());
      beslutsrad.setBeslutsTyp(beslutsradDTO.beslutsTyp());
      beslutsrad.setBeslutsUtfall(beslutsradDTO.beslutsUtfall());
      beslutsrad.setSakfragorStallningstaganden(
            beslutsradDTO.sakfragorStallningstaganden().stream().map(this::toSakfragaStallningstagandeRef).toList());

      return beslutsrad;
   }

   private SakfragaStallningstagandeRef toSakfragaStallningstagandeRef(
         SakfragaStallningstagandeRefDTO sakfragaStallningstagandeRefDTO)
   {
      SakfragaStallningstagandeRef sakfragaStallningstagandeRef = new SakfragaStallningstagandeRef();
      sakfragaStallningstagandeRef.setId(sakfragaStallningstagandeRefDTO.id());
      sakfragaStallningstagandeRef.setVersion(sakfragaStallningstagandeRefDTO.version());

      return sakfragaStallningstagandeRef;
   }

   private Handlaggning toHandlaggning(HandlaggningDTO handlaggningDTO)
   {
      Handlaggning handlagning = new Handlaggning();
      handlagning.setId(handlaggningDTO.id());
      handlagning.setYrkande(toYrkande(handlaggningDTO.yrkande()));
      handlagning.setVersion(handlaggningDTO.version());
      handlagning.setHandlaggningIdTyp(handlaggningDTO.handlaggningIdTyp());
      handlagning.setHandlaggningIdVarde(handlaggningDTO.handlaggningIdVarde());
      handlagning.setSkapadTS(handlaggningDTO.skapadTS());
      handlagning.setAvslutadTS(handlaggningDTO.avslutadTS());
      handlagning.setHandlaggningspecifikationId(handlaggningDTO.handlaggningspecifikationId());

      return handlagning;
   }
}
