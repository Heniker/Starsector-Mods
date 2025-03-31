package mods.ir.data.hullmods;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.loading.HullModSpecAPI;

import mods.common.MyMisc;
import mods.ir.data.config.HE_Settings;

/**
 * Hullmod that converts Ore into Metals
 */
public class HE_ImprovisedRefinery extends BaseLogisticsHullMod {
   public static final Logger log = Global.getLogger(HE_ImprovisedRefinery.class);

   public static final String ID = "HE_ImprovisedRefinery";
   // space out updates to prevent lot's recalculations for in-game stuff & better
   // compitability w/ other mods. Set to 0 for insta updates
   public static float DAYS_TO_TRIGGER = HE_Settings.getDaysToTrigger();

   public static String COMMODITY_FROM = HE_Settings.getCommodityFrom();
   public static String COMMODITY_TO = HE_Settings.getCommodityTo();
   public static float CONVERSION_TAX = HE_Settings.getConversionTax();
   public static float PRISTINE_N_TAX_BONUS_FLAT = HE_Settings.getPristineNanoforgeTaxBonus();
   public static float BASE_CONVERSION_SPEED = HE_Settings.getBaseConversionSpeed(); // units per day
   public static float SMOD_BONUS_RATE = HE_Settings.getSmodBonusRate();
   public static float NANOFORGE_BONUS_RATE = HE_Settings.getNanoforgeBonusRate();

   // Industrial Evolution crashes if this is declared as static
   public final float BASE_CONVERSION_RATIO = MyMisc.getCommodityConversionRatio(HE_Settings.getCommodityFrom(),
         HE_Settings.getCommodityTo());

   public static boolean getEnabledForPlayerFleet() {
      return Global.getSector().getPlayerFleet() != null
            && Global.getSector().getPlayerFleet().getAbility("HE_AbilityToggle") != null &&
            Global.getSector().getPlayerFleet().getAbility("HE_AbilityToggle").isActive();
   }

   public static float getConversionRatePerDay(FleetMemberAPI member, HullModSpecAPI spec) {
      float conversionRate = BASE_CONVERSION_SPEED;

      if (member == null || member.getFleetData() == null || member.getFleetData().getFleet() == null
            || member.getFleetData().getFleet().getCargo() == null || spec == null) {
         return conversionRate;
      }

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

      if (MyMisc.isSMod(member, spec)) {
         conversionRate *= SMOD_BONUS_RATE;
      }

      return conversionRate;
   }

   public float getRecievedMetals(CargoAPI cargo, float usedOre) {
      float tax = cargo != null ? cargo.getQuantity(CargoItemType.SPECIAL,
            new SpecialItemData(Items.PRISTINE_NANOFORGE, null)) >= 1 ? CONVERSION_TAX + PRISTINE_N_TAX_BONUS_FLAT
                  : CONVERSION_TAX
            : 1;

      return MyMisc.round(usedOre * BASE_CONVERSION_RATIO * tax, 2);
   }

   @Override
   public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round((1f - CONVERSION_TAX) * 100) + "%";
      return null;
   }

   @Override
   public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {
      if (index == 0)
         return "" + (int) Math.round((SMOD_BONUS_RATE - 1f) * 100) + "%";
      return null;
   }

   public static class State {
      public float daysSinceLastTrigger;
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

      if (isInPlayerFleet(member.getStats()) && !getEnabledForPlayerFleet()) {
         return;
      }

      data.daysSinceLastTrigger += Global.getSector().getClock().convertToDays(amount);

      if (data.daysSinceLastTrigger > DAYS_TO_TRIGGER && amount != 0) {
         return;
      }

      float days = data.daysSinceLastTrigger;
      data.daysSinceLastTrigger = 0;

      try {
         float hasOre = member.getFleetData().getFleet().getCargo().getCommodityQuantity(COMMODITY_FROM);
         float usedOre = getConversionRatePerDay(member, this.spec) * days;

         CargoAPI cargo = member.getFleetData().getFleet().getCargo();
         if (hasOre > usedOre) {
            cargo.removeCommodity(COMMODITY_FROM, usedOre);
            cargo.addCommodity(COMMODITY_TO, getRecievedMetals(cargo, usedOre));
         } else {
            cargo.removeCommodity(COMMODITY_FROM, hasOre);
            cargo.addCommodity(COMMODITY_TO, getRecievedMetals(cargo, hasOre));
         }
      } catch (NullPointerException err) {
         // this actually never happened
         log.error("Something unexpected happened. Probably the ship had no cargo storage. member, data:");
         log.error(member);
         log.error(data);
         data.daysSinceLastTrigger -= 10000000;
      }

   }

   @Override
   public boolean isApplicableToShip(ShipAPI ship) {
      return ship.getVariant().hasHullMod(HullMods.CIVGRADE) && ship.isCapital() && super.isApplicableToShip(ship);
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

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod(HullMods.CIVGRADE)) {
			return "Can only be installed on civilian-grade hulls";
		}
		return super.getUnapplicableReason(ship);
	}
}
