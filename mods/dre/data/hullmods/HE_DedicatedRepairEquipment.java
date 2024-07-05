package mods.dre.data.hullmods;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import java.awt.Color;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import mods.common.MyMisc;
import mods.dre.data.config.HE_Settings;

/**
 * Hullmod that increases repair rate of a single ship and
 * reduces supply consumption during CR recovery at the cost of metals
 */
public class HE_DedicatedRepairEquipment extends BaseLogisticsHullMod {
   public static Logger log = Global.getLogger(HE_DedicatedRepairEquipment.class);
   public static final String ID = "HE_DedicatedRepairEquipment";
   public static final String BUFF_ID = "repair_equipment_bonus";

   public static final float DAYS_TO_TRIGGER = HE_Settings.getDaysToTrigger();

   public static final float REPAIR_BONUS = HE_Settings.getRepairBonus();
   public static final float SUPPLIES_RECOVERY_BONUS = HE_Settings.getSuppliesRecoveryBonus();
   public static final float MIN_CR = 0.1F;
   public static final String COMMODITY_USED = HE_Settings.getCommodityUsed();
   private static final float USAGE_TAX = HE_Settings.getUsageTax();

   private final float BASE_CONVERSION_RATIO = MyMisc.getCommodityConversionRatio(Commodities.SUPPLIES, HE_Settings.getCommodityUsed());

   public static boolean isValidForRepair(FleetMemberAPI repairShip, FleetMemberAPI repairTarget) {
      return repairShip != null && repairTarget != null && repairTarget != repairShip
            && repairShip.getFleetData() != null
            && repairShip.getFleetData().getFleet() != null
            && repairShip.getFleetData().getFleet().getCargo().getCommodityQuantity(
                  COMMODITY_USED) > 0
            && repairShip.getRepairTracker().getCR() >= MIN_CR
            && !repairShip.getRepairTracker().isSuspendRepairs()
            && MyMisc.isInFleet(repairShip.getFleetData(), repairTarget)
            && (repairTarget.getRepairTracker().getBaseCR() < repairTarget.getRepairTracker().getMaxCR()
                  || repairTarget.getRepairTracker().getRemainingRepairTime() > 0)
            && !repairTarget.getRepairTracker().isSuspendRepairs()
            && !repairTarget.getRepairTracker().isMothballed()
            && !repairTarget.getRepairTracker().isCrashMothballed();
   }

   public static FleetMemberAPI getNewRepairTarget(FleetMemberAPI member) {
      for (FleetMemberAPI it : member.getFleetData().getMembersInPriorityOrder()) {
         if (isValidForRepair(member, it)
               && (it.getBuffManager().getBuff(BUFF_ID) == null
                     || it.getBuffManager().getBuff(BUFF_ID).isExpired())) {
            return it;
         }
      }
      return null;
   }

   public static boolean isRepairInProgress(Buff buffInstance, FleetMemberAPI repairTarget) {
      return repairTarget != null && buffInstance != null
            && repairTarget.getBuffManager().getBuff(BUFF_ID) == buffInstance
            && !buffInstance.isExpired();
   }

   public float getUsedMetalsPerDay(MutableShipStatsAPI stats) {
      return MyMisc.round(MyMisc.getRecoverySuppliesPerDay(stats) * BASE_CONVERSION_RATIO * USAGE_TAX, 2);
   }

   public class RepairEquipmentBuff implements Buff {

      public boolean expired = false;
      private FleetMemberAPI repairTarget;
      private FleetMemberAPI repairShip;
      private CargoAPI cargo;

      private float metalsPerDay;

      public RepairEquipmentBuff(FleetMemberAPI repairShip, FleetMemberAPI repairTarget) {
         this.repairTarget = repairTarget;
         this.repairShip = repairShip;
         this.metalsPerDay = getUsedMetalsPerDay(repairTarget.getStats());
         this.cargo = repairShip.getFleetData().getFleet().getCargo();
      }

      public boolean isExpired() {
         return expired;
      }

      public String getId() {
         return BUFF_ID;
      }

      public void apply(FleetMemberAPI member) {
         member.getStats().getSuppliesToRecover().modifyMult(getId(), SUPPLIES_RECOVERY_BONUS);
         member.getStats().getRepairRatePercentPerDay().modifyMult(getId(),
               REPAIR_BONUS);
      }

      // yes. this uses actual in-game days.
      // no, I dont know why.
      public void advance(float days) {
         if (!isValidForRepair(repairShip, repairTarget)) {
            expired = true;
            return;
         }

         float metalsShouldUse = metalsPerDay * days;
         cargo.removeCommodity(COMMODITY_USED, metalsShouldUse);

      }
   };

   public static class State {
      public float daysSinceLastTrigger;
      public FleetMemberAPI repairTarget;
      public RepairEquipmentBuff buffInstance;
   }

   public static Map<FleetMemberAPI, State> state = new WeakHashMap<FleetMemberAPI, State>();

   @Override
   public void advanceInCampaign(FleetMemberAPI member, float amount) {
      State data = state.get(member);

      if (data == null) {
         State s = new State();
         s.daysSinceLastTrigger = 0;
         data = state.put(member, s);
         data = s;
      }

      data.daysSinceLastTrigger += Global.getSector().getClock().convertToDays(amount);

      if (data.daysSinceLastTrigger > DAYS_TO_TRIGGER && amount != 0) {
         return;
      }

      float days = data.daysSinceLastTrigger;
      data.daysSinceLastTrigger = 0;

      if (isRepairInProgress(data.buffInstance, data.repairTarget)) {
         return;
      }

      FleetMemberAPI newRepairTarget = getNewRepairTarget(member);
      if (newRepairTarget == null) {
         return;
      }

      data.repairTarget = newRepairTarget;

      data.buffInstance = new RepairEquipmentBuff(member, data.repairTarget);
      data.repairTarget.getBuffManager().addBuffOnlyUpdateStat(data.buffInstance);
   }

   @Override
   public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round((REPAIR_BONUS - 1F) * 100) + "%";
      if (index == 1)
         return "" + (int) Math.round(SUPPLIES_RECOVERY_BONUS * 100) + "%";
      return null;
   }

   @Override
   public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
      return true;
   }

   @Override
   public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
         boolean isForModSpec) {
      if (Global.getSettings().getCurrentState() == GameState.TITLE || isForModSpec || ship == null
            || ship.getFleetMember() == null) {
         return;
      }

      float pad = 3f;
      float opad = 10f;
      Color h = Misc.getHighlightColor();
      Color bad = Misc.getNegativeHighlightColor();

      State data = state.get(ship.getFleetMember());

      if (data == null) {
         return;
      }

      if (isRepairInProgress(data.buffInstance, data.repairTarget)) {
         tooltip.addPara("The ship is currently repairing %s. The cost is %s " + COMMODITY_USED + " per day.", opad, h,
               "" + data.repairTarget.getShipName(),
               "" + (int) Math.round(getUsedMetalsPerDay(data.repairTarget.getStats())));
      } else if (ship.getFleetMember().getFleetData().getFleet().getCargo().getCommodityQuantity(
            COMMODITY_USED) < 1) {
         tooltip.addPara("The ship is lacking " + COMMODITY_USED + " for repair.", opad, h);
      } else if (ship.getFleetMember().getRepairTracker().getCR() < MIN_CR) {
         // impl/campaign/RepairGantry.java
         LabelAPI label = tooltip.addPara("This ship's combat readiness is below %s " +
               "and the repairs can not be conducted.",
               opad, h,
               "" + (int) Math.round(MIN_CR * 100f) + "%");
         label.setHighlightColors(bad, h);
         label.setHighlight("" + (int) Math.round(MIN_CR * 100f) + "%");
      } else {
         tooltip.addPara("The ship is not repairing anything currently.", opad, h);
      }
   }

   @Override
   public boolean isApplicableToShip(ShipAPI ship) {
      return false;
   }

   @Override
   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
      FleetMemberAPI member = stats.getFleetMember();
      State data = state.get(member);

      if (data == null) {
         State s = new State();
         s.daysSinceLastTrigger = 0;
         data = state.put(member, s);
         data = s;
      }
   }
}
