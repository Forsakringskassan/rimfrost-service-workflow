package se.fk.github.rimfrost.workflow.logic.util;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableProduceratResultatDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IndividYrkandeRollDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ProduceratResultatDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeDTO;
import se.fk.rimfrost.framework.handlaggning.model.Handlaggning;
import se.fk.rimfrost.framework.handlaggning.model.HandlaggningUpdate;
import se.fk.rimfrost.framework.handlaggning.model.Idtyp;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIdtyp;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIndividYrkandeRoll;
import se.fk.rimfrost.framework.handlaggning.model.IndividYrkandeRoll;
import se.fk.rimfrost.framework.handlaggning.model.ProduceratResultat;
import se.fk.rimfrost.framework.handlaggning.model.Yrkande;

@ApplicationScoped
public class LogicMapper
{
   /**
    * Maps a {@link Handlaggning} read from the handlaggning adapter to {@link HandlaggningDTO}.
    */
   public HandlaggningDTO toHandlaggningDTO(Handlaggning handlaggning)
   {
      return ImmutableHandlaggningDTO.builder()
            .id(handlaggning.id())
            .yrkande(toYrkandeDTO(handlaggning.yrkande()))
            .version(handlaggning.version())
            .processinstansId(handlaggning.processInstansId())
            .handlaggningspecifikationId(handlaggning.handlaggningspecifikationId())
            .skapadTS(handlaggning.skapadTS())
            .avslutadTS(handlaggning.avslutadTS())
            .build();
   }

   /**
    * Maps a {@link HandlaggningUpdate} to {@link HandlaggningDTO}.
    */
   public HandlaggningDTO toHandlaggningDTO(HandlaggningUpdate handlaggningUpdate)
   {
      return ImmutableHandlaggningDTO.builder()
            .id(handlaggningUpdate.id())
            .yrkande(toYrkandeDTO(handlaggningUpdate.yrkande()))
            .version(handlaggningUpdate.version())
            .processinstansId(handlaggningUpdate.processInstansId())
            .handlaggningspecifikationId(handlaggningUpdate.handlaggningspecifikationId())
            .skapadTS(handlaggningUpdate.skapadTS())
            .build();
   }

   public YrkandeDTO toYrkandeDTO(Yrkande yrkande)
   {
      return ImmutableYrkandeDTO.builder()
            .id(yrkande.id())
            .erbjudandeId(yrkande.erbjudandeId())
            .version(yrkande.version())
            .yrkandedatum(yrkande.yrkandeDatum())
            .yrkandeFrom(yrkande.yrkandeFrom())
            .yrkandeTom(yrkande.yrkandeTom())
            .yrkandestatus(yrkande.yrkandeStatus())
            .avsikt(yrkande.avsikt())
            .individYrkandeRoll(yrkande.individYrkandeRoller()
                  .stream()
                  .map(this::toIndividYrkandeRollDTO)
                  .toList())
            .produceradeResultat(yrkande.produceradeResultat()
                  .stream()
                  .map(this::toProduceratResultatDTO)
                  .toList())
            .build();
   }

   public IndividYrkandeRollDTO toIndividYrkandeRollDTO(IndividYrkandeRoll individYrkandeRollEntity)
   {
      return ImmutableIndividYrkandeRollDTO.builder()
            .individ(toIdtypDTO(individYrkandeRollEntity.individ()))
            .yrkandeRollId(individYrkandeRollEntity.yrkandeRollId())
            .build();
   }

   public IndividYrkandeRoll toIndividYrkandeRoll(IndividYrkandeRollDTO individYrkandeRoll)
   {
      return ImmutableIndividYrkandeRoll.builder()
            .individ(toIdtyp(individYrkandeRoll.individ()))
            .yrkandeRollId(individYrkandeRoll.yrkandeRollId())
            .build();
   }

   public Idtyp toIdtyp(IdtypDTO idtyp)
   {
      return ImmutableIdtyp.builder()
            .typId(idtyp.typId())
            .varde(idtyp.varde())
            .build();
   }

   public IdtypDTO toIdtypDTO(Idtyp idtyp)
   {
      return ImmutableIdtypDTO.builder()
            .typId(idtyp.typId())
            .varde(idtyp.varde())
            .build();
   }

   public ProduceratResultatDTO toProduceratResultatDTO(ProduceratResultat produceratResultat)
   {
      return ImmutableProduceratResultatDTO.builder()
            .id(produceratResultat.id())
            .version(produceratResultat.version())
            .franOchMed(produceratResultat.resultatFrom())
            .tillOchMed(produceratResultat.resultatTom())
            .yrkandestatus(produceratResultat.yrkandeStatus())
            .avslagsanledning(produceratResultat.avslagsanledning())
            .typ(produceratResultat.typ())
            .data(produceratResultat.data())
            .build();
   }
}
