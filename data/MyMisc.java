package data;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
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
}
