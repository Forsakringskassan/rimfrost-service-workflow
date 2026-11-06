package se.fk.github.rimfrost.workflow.logic.util;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.github.rimfrost.workflow.logic.dto.BeslutDTO;
import se.fk.github.rimfrost.workflow.logic.dto.BeslutsradDTO;
import se.fk.github.rimfrost.workflow.logic.dto.HandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.IdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableBeslutDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableBeslutsradDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableHandlaggningDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableIdtypDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableRollIYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableSakfragaStallningstagandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableSakfragaStallningstagandeRefDTO;
import se.fk.github.rimfrost.workflow.logic.dto.ImmutableYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.RollIYrkandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeDTO;
import se.fk.github.rimfrost.workflow.logic.dto.SakfragaStallningstagandeRefDTO;
import se.fk.github.rimfrost.workflow.logic.dto.YrkandeDTO;
import se.fk.rimfrost.framework.handlaggning.model.Beslut;
import se.fk.rimfrost.framework.handlaggning.model.Beslutsrad;
import se.fk.rimfrost.framework.handlaggning.model.Handlaggning;
import se.fk.rimfrost.framework.handlaggning.model.Idtyp;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIdtyp;
import se.fk.rimfrost.framework.handlaggning.model.RollIYrkande;
import se.fk.rimfrost.framework.handlaggning.model.SakfragaStallningstagandeContainer;
import se.fk.rimfrost.framework.handlaggning.model.SakfragaStallningstagandeRef;
import se.fk.rimfrost.framework.handlaggning.model.Yrkande;

@ApplicationScoped
public class LogicMapper
{
   /**
    * Maps a {@link Handlaggning} from the handlaggning adapter to {@link HandlaggningDTO}.
    */
   public HandlaggningDTO toHandlaggningDTO(Handlaggning handlaggning)
   {
      return ImmutableHandlaggningDTO.builder()
            .id(handlaggning.id())
            .yrkande(toYrkandeDTO(handlaggning.yrkande()))
            .version(handlaggning.version())
            .handlaggningIdTyp(handlaggning.handlaggningIdTyp())
            .handlaggningIdVarde(handlaggning.handlaggningIdVarde())
            .handlaggningspecifikationId(handlaggning.handlaggningspecifikationId())
            .skapadTS(handlaggning.skapadTS())
            .avslutadTS(handlaggning.avslutadTS())
            .build();
   }

   public YrkandeDTO toYrkandeDTO(Yrkande yrkande)
   {
      return ImmutableYrkandeDTO.builder()
            .id(yrkande.id())
            .ingangtypId(yrkande.ingangtypId())
            .version(yrkande.version())
            .yrkandedatum(yrkande.yrkandeDatum())
            .yrkandeFrom(yrkande.yrkandeFrom())
            .yrkandeTom(yrkande.yrkandeTom())
            .yrkandestatus(yrkande.yrkandeStatus())
            .avsikt(yrkande.avsikt())
            .rollerIYrkande(yrkande.rollerIYrkande()
                  .stream()
                  .map(this::toRollIYrkandeDTO)
                  .toList())
            .sakfragorStallningstaganden(yrkande.sakfragorStallningstaganden()
                  .stream()
                  .map(this::toSakfragaStallningstagandeDTO)
                  .toList())
            .beslut(yrkande.beslut()
                  .stream()
                  .map(this::toBeslutDTO)
                  .toList())
            .build();
   }

   public RollIYrkandeDTO toRollIYrkandeDTO(RollIYrkande rollIYrkande)
   {
      return ImmutableRollIYrkandeDTO.builder()
            .id(rollIYrkande.id())
            .individ(toIdtypDTO(rollIYrkande.individ()))
            .yrkandeRollId(rollIYrkande.yrkandeRollId())
            .avserYrkande(rollIYrkande.avserYrkande())
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

   public SakfragaStallningstagandeDTO toSakfragaStallningstagandeDTO(
         SakfragaStallningstagandeContainer sakfragaStallningstagande)
   {
      return ImmutableSakfragaStallningstagandeDTO.builder()
            .id(sakfragaStallningstagande.id())
            .objektTypId(sakfragaStallningstagande.objektTypId())
            .data(sakfragaStallningstagande.data())
            .build();
   }

   public BeslutDTO toBeslutDTO(Beslut beslut)
   {
      return ImmutableBeslutDTO.builder()
            .id(beslut.id())
            .version(beslut.version())
            .datum(beslut.datum())
            .beslutsfattare(toIdtypDTO(beslut.beslutsfattare()))
            .beslutsrader(beslut.beslutsrader()
                  .stream()
                  .map(this::toBeslutsradDTO)
                  .toList())
            .build();
   }

   public BeslutsradDTO toBeslutsradDTO(Beslutsrad beslutsrad)
   {
      return ImmutableBeslutsradDTO.builder()
            .id(beslutsrad.id())
            .version(beslutsrad.version())
            .beslutsTyp(beslutsrad.beslutsTyp())
            .beslutsUtfall(beslutsrad.beslutsUtfall())
            .avslutsTyp(beslutsrad.avslutsTyp())
            .sakfragorStallningstaganden(beslutsrad.sakfragorStallningstaganden()
                  .stream()
                  .map(this::toSakfragaStallningstagandeRefDTO)
                  .toList())
            .build();
   }

   public SakfragaStallningstagandeRefDTO toSakfragaStallningstagandeRefDTO(
         SakfragaStallningstagandeRef sakfragaStallningstagandeRef)
   {
      return ImmutableSakfragaStallningstagandeRefDTO.builder()
            .id(sakfragaStallningstagandeRef.id())
            .version(sakfragaStallningstagandeRef.version())
            .build();
   }
}
