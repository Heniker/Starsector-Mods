package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.loading.HullModSpecAPI;

import data.Misc;

/**
 * Hull mod that converts Ore into Rare ore
 */
public class IR_ImprovisedRefinery extends BaseLogisticsHullMod {
   public static final float USAGE_TAX = 0.75F;
   public static final float SMOD_BONUS_RATE = 1.5F;
   public static final int BASE_CONVERSION_RATE = 50;
   public static boolean EnbabledForPlayerFleet = false;

   public float daysSinceLastTrigger = 0F;

   public static int getConversionRate(FleetMemberAPI member, HullModSpecAPI spec) {
      boolean sMod = Misc.isSMod(member.getStats(), spec);

      float conversionRate = sMod ? BASE_CONVERSION_RATE * SMOD_BONUS_RATE : BASE_CONVERSION_RATE;
      return Math.round(conversionRate);
   }

   public static int getUsedOre(FleetMemberAPI member, HullModSpecAPI spec) {
      float conversionRate = getConversionRate(member, spec);
      float oreAmount = member.getFleetData().getFleet().getCargo().getCommodityQuantity(Commodities.ORE);
      float oreUsed = oreAmount > conversionRate ? conversionRate : oreAmount;

      return Math.round(oreUsed);
   }

   public static int getRecievedRareOre(FleetMemberAPI member, int usedOre) {
      float orePrice = Global.getSettings().getCommoditySpec(Commodities.ORE).getBasePrice();
      float rareOrePrice = Global.getSettings().getCommoditySpec(Commodities.RARE_ORE).getBasePrice();

      float conversionBudget = orePrice * usedOre * USAGE_TAX;
      return Math.round(conversionBudget / rareOrePrice);
   }

   public String getDescriptionParam(int index) {
      if (index == 0)
         return "" + (int) Math.round(USAGE_TAX * 100) + "%";
      return null;
   }

   public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round(SMOD_BONUS_RATE * 100) + "%";
      return null;
   }

   public void advanceInCampaign(FleetMemberAPI member, float amount) {
      if (isInPlayerFleet(member.getStats()) && !EnbabledForPlayerFleet) {
         return;
      }

      float days = Global.getSector().getClock().convertToDays(amount);
      this.daysSinceLastTrigger += days;

      if (this.daysSinceLastTrigger >= 1) {
         this.daysSinceLastTrigger = 1 - daysSinceLastTrigger;
         CargoAPI cargo = member.getFleetData().getFleet().getCargo();

         int usedOre = getUsedOre(member, this.spec);
         if (usedOre > 0) {
            cargo.removeCommodity(Commodities.ORE, usedOre);
            cargo.addCommodity(Commodities.RARE_ORE, getRecievedRareOre(member, usedOre));
         }
      }
   }

   public boolean isApplicableToShip(ShipAPI ship) {
      return ship.isCapital();
   }
}
