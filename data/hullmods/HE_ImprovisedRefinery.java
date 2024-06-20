package data.hullmods;

import java.util.Map;
import java.util.WeakHashMap;

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

public class HE_ImprovisedRefinery extends BaseLogisticsHullMod {
   public static final float DAYS_TO_TRIGGER = 0.2F;

   public static final String COMMODITY_FROM = Commodities.ORE;
   public static final String COMMODITY_TO = Commodities.METALS;
   public static final float CONVERSION_TAX = 0.75F;
   public static final float PRISTINE_N_TAX_BONUS_FLAT = 0.1F;
   public static final float BASE_CONVERSION_RATIO = MyMisc.getCommodityConversionRatio(COMMODITY_FROM,
         COMMODITY_TO);
   public static final float BASE_CONVERSION_SPEED = 72; // units per day
   public static final float SMOD_BONUS_RATE = 1.4F;
   public static final float NANOFORGE_BONUS_RATE = 1.5F;

   // hack to allow ability toggle to work
   public static boolean getEnabledForPlayerFleet() {
      return Global.getSector().getPlayerFleet().getAbility("HE_AbilityToggle")
            .isActive();
   }

   public static float getConversionRatePerDay(FleetMemberAPI member, HullModSpecAPI spec) {
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

   public static float getRecievedMetals(FleetMemberAPI member, float usedOre) {
      float tax = member.getFleetData().getFleet().getCargo().getQuantity(CargoItemType.SPECIAL,
            new SpecialItemData(Items.PRISTINE_NANOFORGE, null)) >= 1 ? CONVERSION_TAX + PRISTINE_N_TAX_BONUS_FLAT
                  : CONVERSION_TAX;

      return MyMisc.round(BASE_CONVERSION_RATIO * usedOre * tax, 2);
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
         return "" + (int) Math.round(SMOD_BONUS_RATE * 100) + "%";
      return null;
   }

   class State {
      public float nextTriggerIn;
      public boolean isInPlayerFleet;
   }

   public Map<FleetMemberAPI, State> state = new WeakHashMap<FleetMemberAPI, State>();

   @Override
   public void advanceInCampaign(FleetMemberAPI member, float amount) {
      State data = state.get(member);

      if (data == null) {
         State s = new State();
         s.isInPlayerFleet = isInPlayerFleet(member.getStats());
         s.nextTriggerIn = DAYS_TO_TRIGGER;
         data = state.put(member, s);
         data = s;
      }

      if (data.isInPlayerFleet && !getEnabledForPlayerFleet()) {
         return;
      }

      if (data.nextTriggerIn > 0) {
         data.nextTriggerIn -= Global.getSector().getClock().convertToDays(amount);
         return;
      }

      data.nextTriggerIn = DAYS_TO_TRIGGER + data.nextTriggerIn;

      float hasOre = member.getFleetData().getFleet().getCargo().getCommodityQuantity(COMMODITY_FROM);
      float usedOre = getConversionRatePerDay(member, this.spec) * DAYS_TO_TRIGGER;

      if (hasOre > 0) {
         CargoAPI cargo = member.getFleetData().getFleet().getCargo();
         cargo.removeCommodity(COMMODITY_FROM, usedOre);
         cargo.addCommodity(COMMODITY_TO, getRecievedMetals(member, usedOre));
      }
   }

   @Override
   public boolean isApplicableToShip(ShipAPI ship) {
      return super.isApplicableToShip(ship) && ship.isCapital();
   }
}
