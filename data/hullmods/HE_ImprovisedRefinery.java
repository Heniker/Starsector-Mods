package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.loading.HullModSpecAPI;

import data.MyMisc;

/**
 * Hull mod that converts Ore into Metals
 */
public class HE_ImprovisedRefinery extends BaseLogisticsHullMod {
   public static final float DAYS_TO_TRIGGER = 0.3F;

   public static final float USAGE_TAX = 0.75F;
   public static final float PRISTINE_N_TAX_BONUS_FLAT = 0.1F;
   public static final float BASE_CONVERSION_SPEED = DAYS_TO_TRIGGER * 72; // units per trigger
   public static final float SMOD_BONUS_RATE = 1.4F;
   public static final float NANOFORGE_BONUS_RATE = 1.5F;

   public static boolean EnabledForPlayerFleet = false;

   public static float getConversionRate(FleetMemberAPI member, HullModSpecAPI spec) {
      float conversionRate = BASE_CONVERSION_SPEED;
      CargoAPI cargo = member.getFleetData().getFleet().getCargo();

      if (cargo.getQuantity(CargoItemType.SPECIAL,
            new SpecialItemData(Items.PRISTINE_NANOFORGE, null)) >= 1) {
         conversionRate *= NANOFORGE_BONUS_RATE;
      } else if (cargo.getQuantity(CargoItemType.SPECIAL,
            new SpecialItemData(Items.CORRUPTED_NANOFORGE, null)) >= 1) {

         conversionRate *= NANOFORGE_BONUS_RATE;
         conversionRate *= member.getRepairTracker().getCR();
      } else {
         conversionRate *= member.getRepairTracker().getCR();
      }

      if (MyMisc.isSMod(member.getStats(), spec)) {
         conversionRate *= SMOD_BONUS_RATE;
      }

      return conversionRate;
   }

   public static float getUsedOre(FleetMemberAPI member, HullModSpecAPI spec) {
      float conversionRate = getConversionRate(member, spec);
      float oreAmount = member.getFleetData().getFleet().getCargo().getCommodityQuantity(Commodities.ORE);
      float oreUsed = oreAmount > conversionRate ? conversionRate : oreAmount;

      return oreUsed;
   }

   public static float getRecievedRareOre(FleetMemberAPI member, float usedOre) {
      float tax = member.getFleetData().getFleet().getCargo().getQuantity(CargoItemType.SPECIAL,
            new SpecialItemData(Items.PRISTINE_NANOFORGE, null)) >= 1 ? USAGE_TAX + PRISTINE_N_TAX_BONUS_FLAT
                  : USAGE_TAX;

      return MyMisc.convertCommodityByValue(Commodities.ORE, Commodities.METALS, usedOre) * tax;
   }

   public float nextTriggerIn = 0F;

   @Override
   public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round(USAGE_TAX * 100) + "%";
      return null;
   }

   @Override
   public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round(SMOD_BONUS_RATE * 100) + "%";
      return null;
   }

   @Override
   public void advanceInCampaign(FleetMemberAPI member, float amount) {
      if (isInPlayerFleet(member.getStats()) && !EnabledForPlayerFleet) {
         return;
      }

      if (nextTriggerIn > 0) {
         nextTriggerIn -= Global.getSector().getClock().convertToDays(amount);
         return;
      }

      nextTriggerIn = DAYS_TO_TRIGGER + nextTriggerIn;

      float usedOre = getUsedOre(member, this.spec);

      if (usedOre > 0) {
         CargoAPI cargo = member.getFleetData().getFleet().getCargo();
         cargo.removeCommodity(Commodities.ORE, usedOre);
         cargo.addCommodity(Commodities.RARE_ORE, getRecievedRareOre(member, usedOre));
      }
   }

   @Override
   public boolean isApplicableToShip(ShipAPI ship) {
      return super.isApplicableToShip(ship) && ship.isCapital();
   }
}
