package data.hullmods;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.BuffManagerAPI;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.RepairTrackerAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.loading.HullModSpecAPI;

import data.MyMisc;

/**
 * Hull mod that increases repair rate of a single ship and
 * reduces supply consumption during CR recovery at the cost of metals
 */
public class HE_DedicatedRepairEquipment extends BaseLogisticsHullMod {
   public static final String BONUS_ID = "repair_equipment_bonus";
   public static final float REPAIR_BONUS = 1.5F;
   public static final float SUPPLIES_RECOVERY_BONUS = 0.5F;
   public static final float DAYS_TO_TRIGGER = 0.2F;
   public static final float CR_REPAIR = 0.1F;

   public static boolean isValidForRepair(FleetMemberAPI repairShip, FleetMemberAPI repairTarget) {
      return repairShip != null && repairTarget != null && repairTarget != repairShip
            && repairShip.getFleetData() != null
            && repairShip.getRepairTracker().getCR() >= 0.1F
            && repairShip.getFleetData().getMembersInPriorityOrder().contains(repairTarget)
            && !repairShip.getRepairTracker().isSuspendRepairs()
            && repairTarget.getRepairTracker().getCR() < repairTarget.getRepairTracker().getMaxCR()
            && !repairTarget.getRepairTracker().isSuspendRepairs()
            && !repairTarget.getRepairTracker().isMothballed()
            && !repairTarget.getRepairTracker().isCrashMothballed();
   }

   public static FleetMemberAPI getValidRepairTarget(FleetMemberAPI member) {
      for (FleetMemberAPI it : member.getFleetData().getMembersInPriorityOrder()) {
         if (isValidForRepair(member, it)) {
            return it;
         }
      }
      return null;
   }

   public static class RepairEquipmentBuff implements Buff {
      private FleetMemberAPI repairTarget;
      private FleetMemberAPI repairShip;
      private boolean expired = false;

      public RepairEquipmentBuff(FleetMemberAPI repairShip, FleetMemberAPI repairTarget) {
         this.repairTarget = repairTarget;
         this.repairShip = repairShip;
      }

      public boolean isExpired() {
         return expired && (expired = isValidForRepair(repairShip, repairTarget));
      }

      public String getId() {
         return BONUS_ID;
      }

      public void apply(FleetMemberAPI member) {
         member.getStats().getSuppliesToRecover().modifyMult(getId(), SUPPLIES_RECOVERY_BONUS);
         member.getStats().getRepairRatePercentPerDay().modifyMult(getId(),
               REPAIR_BONUS);
      }

      public void advance(float days) {
         // duration -= days;
      }
   };

   class State {
      public float nextTriggerIn;
      public FleetMemberAPI repairTarget;
      public RepairEquipmentBuff buffInstance;
   }

   @Override
   public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + DAYS_TO_TRIGGER;
      if (index == 1)
         return "" + (int) Math.round(CR_REPAIR * 100) + "%";
      return null;
   }

   @Override
   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
   }

   public Map<FleetMemberAPI, State> state = new WeakHashMap<FleetMemberAPI, State>();

   @Override
   public void advanceInCampaign(FleetMemberAPI member, float amount) {
      State data = state.get(member);

      if (data == null) {
         State s = new State();
         data = state.put(member, s);
         data = s;
      }

      if (data.nextTriggerIn >= 0) {
         data.nextTriggerIn -= Global.getSector().getClock().convertToDays(amount);
         return;
      }

      if (data.repairTarget != null && data.buffInstance != null && !data.buffInstance.isExpired()) {
         data.nextTriggerIn = DAYS_TO_TRIGGER - data.nextTriggerIn;
         return;
      }

      FleetMemberAPI newRepairTarget = getValidRepairTarget(member);
      if (newRepairTarget == null || newRepairTarget.getBuffManager().getBuff(BONUS_ID) != null) {
         data.nextTriggerIn = DAYS_TO_TRIGGER - data.nextTriggerIn;
         return;
      }

      // from here on we have valid data.repairTarget
      data.repairTarget = newRepairTarget;

      data.buffInstance = new RepairEquipmentBuff(member, data.repairTarget);
      data.repairTarget.getBuffManager().addBuffOnlyUpdateStat(data.buffInstance);

      data.nextTriggerIn = DAYS_TO_TRIGGER - data.nextTriggerIn;
   }

   @Override
   public boolean isApplicableToShip(ShipAPI ship) {
      return true;
   }
}
