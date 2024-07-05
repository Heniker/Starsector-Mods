package mods.common;

import java.math.BigDecimal;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.HullModEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.loading.HullModSpecAPI;

/**
 * Everything here should not throw NullPointer exception
 */
public class MyMisc {
  public static boolean isSMod(FleetMemberAPI member, HullModSpecAPI spec) {
    return member == null ? false : isSMod(member.getStats(), spec);
  }

  public static boolean isSMod(MutableShipStatsAPI stats, HullModSpecAPI spec) {
    if (stats != null && spec != null && stats.getVariant() != null) {
      return stats.getVariant().getSMods().contains(spec.getId())
          || stats.getVariant().getSModdedBuiltIns().contains(spec.getId());
    } else {
      return false;
    }
  }

  /**
   * THIS IS NOT SAFE TO CALL BEFORE GAME LOAD.
   * For whatever reason Industrial Evolution makes the game crash if this
   * is called during sector generation or fleet creation.
   */
  public static float getCommodityConversionRatio(String from, String to) {
    return Global.getSettings().getCommoditySpec(from).getBasePrice()
        / Global.getSettings().getCommoditySpec(to).getBasePrice();

  }

  /**
   * Supplies needed per day to recover CR / repair ship
   */
  public static float getRecoverySuppliesPerDay(MutableShipStatsAPI stats) {
    return stats.getSuppliesToRecover().base
        / stats.getVariant().getHullSpec().getCRToDeploy()
        * stats.getBaseCRRecoveryRatePercentPerDay().base;

  }

  public static float round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }

  // combat/BaseHullMod.java:270
  public static boolean isInPlayerFleet(FleetMemberAPI ship) {
    return isInFleet(Global.getSector().getPlayerFleet().getFleetData(), ship);
  }

  public static boolean isInFleet(FleetDataAPI fleetData, FleetMemberAPI ship) {
    if (fleetData == null || ship == null) {
      return false;
    }
    List<FleetMemberAPI> members = fleetData.getMembersInPriorityOrder();
    if (members == null) {
      return false;
    }
    return members.contains(ship);
  }

  public static HullModEffect getHullMod(String id) {
    return Global.getSettings().getHullModSpec(id).getEffect();
  }
}
