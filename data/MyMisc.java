package data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.loading.HullModSpecAPI;

public class MyMisc {
  public static boolean isSMod(MutableShipStatsAPI stats, HullModSpecAPI spec) {
    if (stats != null && stats.getVariant() != null && spec != null) {
      return stats.getVariant().getSMods().contains(spec.getId())
          || stats.getVariant().getSModdedBuiltIns().contains(spec.getId());
    } else {
      return false;
    }
  }

  public static float convertCommodityByValue(String from, String to, float amount) {
    return Global.getSettings().getCommoditySpec(from).getBasePrice() * amount
        / Global.getSettings().getCommoditySpec(to).getBasePrice();

  }

}
