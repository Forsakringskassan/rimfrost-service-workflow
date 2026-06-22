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
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableProduceratResultatCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.IndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ProduceratResultatDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ProduceratResultatRefDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateRequest;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeCreateResponse;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeDTO;
import se.fk.rimfrost.HandlaggningErrorInformation;
import se.fk.rimfrost.HandlaggningResponseMessageData;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Beslut;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Beslutsrad;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Handlaggning;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Idtyp;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.IndividYrkandeRoll;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeResponse;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.ProduceratResultat;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.ProduceratResultatRef;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.Yrkande;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class PresentationMapper
{
   public YrkandeCreateRequest toYrkandeCreateRequest(PostYrkandeRequest postYrkandeRequest)
   {
      var individYrkandeRoller = postYrkandeRequest.getIndividYrkandeRoller().stream()
            .map(e -> ImmutableIndividYrkandeRollDTO.builder()
                  .individ(toIdtypDTO(e.getIndivid()))
                  .yrkandeRollId(e.getYrkandeRollId())
                  .build())
            .toList();

      var produceradeResultat = postYrkandeRequest.getProduceradeResultat().stream()
            .map(e -> ImmutableProduceratResultatCreateRequest.builder()
                  .franOchMed(e.getFrom())
                  .tillOchMed(e.getTom())
                  .typ(e.getTyp())
                  .data(e.getData())
                  .build())
            .toList();

      return ImmutableYrkandeCreateRequest.builder()
            .erbjudandeId(postYrkandeRequest.getErbjudandeId())
            .yrkandeFrom(postYrkandeRequest.getYrkandeFrom())
            .yrkandeTom(postYrkandeRequest.getYrkandeTom())
            .handlaggningspecifikationId(postYrkandeRequest.getHandlaggningspecifikationId())
            .avsiktsId(postYrkandeRequest.getAvsiktsId())
            .replyTo(postYrkandeRequest.getReplyTo())
            .individYrkandeRoller(individYrkandeRoller)
            .produceradeResultat(produceradeResultat)
            .build();
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
      yrkande.setErbjudandeId(yrkandeDTO.erbjudandeId());
      yrkande.setVersion(yrkandeDTO.version());
      yrkande.setYrkandedatum(yrkandeDTO.yrkandedatum());
      yrkande.setYrkandeFrom(yrkandeDTO.yrkandeFrom());
      yrkande.setYrkandeTom(yrkandeDTO.yrkandeTom());
      yrkande.setYrkandestatus(yrkandeDTO.yrkandestatus());
      yrkande.setAvsikt(yrkandeDTO.avsikt());
      yrkande.setIndividYrkandeRoller(
            yrkandeDTO.individYrkandeRoll()
                  .stream()
                  .map(this::toIndividYrkandeRoll)
                  .toList());
      yrkande.setProduceradeResultat(
            yrkandeDTO.produceradeResultat()
                  .stream()
                  .map(this::toProduceratResultat)
                  .toList());
      yrkande.setBeslut(toBeslut(yrkandeDTO.beslut()));
      return yrkande;
   }

   private IndividYrkandeRoll toIndividYrkandeRoll(IndividYrkandeRollDTO individYrkandeRollDTO)
   {
      var invidvidYrkandeRoll = new IndividYrkandeRoll();
      invidvidYrkandeRoll.setIndivid(toIdtyp(individYrkandeRollDTO.individ()));
      invidvidYrkandeRoll.setYrkandeRollId(individYrkandeRollDTO.yrkandeRollId());
      return invidvidYrkandeRoll;
   }

   private ProduceratResultat toProduceratResultat(ProduceratResultatDTO produceratResultatDTO)
   {
      ProduceratResultat produceratResultat = new ProduceratResultat();
      produceratResultat.setId(produceratResultatDTO.id());
      produceratResultat.setVersion(produceratResultatDTO.version());
      produceratResultat.setFrom(produceratResultatDTO.franOchMed());
      produceratResultat.setTom(produceratResultatDTO.tillOchMed());
      produceratResultat.setAvslagsanledning(produceratResultatDTO.avslagsanledning());
      produceratResultat.setYrkandestatus(produceratResultatDTO.yrkandestatus());
      produceratResultat.setTyp(produceratResultatDTO.typ());
      produceratResultat.setData(produceratResultatDTO.data());
      return produceratResultat;
   }

   private Beslut toBeslut(BeslutDTO beslutDTO)
   {
      if (beslutDTO == null)
      {
         return null;
      }

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
      if (beslutsradDTO == null)
      {
         return null;
      }

      Beslutsrad beslutsrad = new Beslutsrad();
      beslutsrad.setId(beslutsradDTO.id());
      beslutsrad.setVersion(beslutsradDTO.version());
      beslutsrad.setAvslutsTyp(beslutsradDTO.avslutsTyp());
      beslutsrad.setBeslutsTyp(beslutsradDTO.beslutsTyp());
      beslutsrad.setBeslutsUtfall(beslutsradDTO.beslutsUtfall());
      beslutsrad.setProduceradeResultatRef(
            beslutsradDTO.produceratResultatRefs().stream().map(this::toProduceratResultatRef).toList());

      return beslutsrad;
   }

   private ProduceratResultatRef toProduceratResultatRef(ProduceratResultatRefDTO produceratResultatRefDTO)
   {
      if (produceratResultatRefDTO == null)
      {
         return null;
      }

      ProduceratResultatRef produceratResultatRef = new ProduceratResultatRef();
      produceratResultatRef.setId(produceratResultatRefDTO.id());
      produceratResultatRef.setVersion(produceratResultatRefDTO.version());

      return produceratResultatRef;
   }

   private Handlaggning toHandlaggning(HandlaggningDTO handlaggningDTO)
   {
      Handlaggning handlagning = new Handlaggning();
      handlagning.setId(handlaggningDTO.id());
      handlagning.setYrkande(toYrkande(handlaggningDTO.yrkande()));
      handlagning.setVersion(handlaggningDTO.version());
      handlagning.setProcessinstansId(handlaggningDTO.processinstansId());
      handlagning.setSkapadTS(handlaggningDTO.skapadTS());
      handlagning.setAvslutadTS(handlaggningDTO.avslutadTS());
      handlagning.setHandlaggningspecifikationId(handlaggningDTO.handlaggningspecifikationId());

      return handlagning;
   }
}
