package mods.dre.data.config;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import lunalib.lunaSettings.LunaSettings;
import mods.common.MyMisc;

public class HE_Settings {
  public static String modId = "HE_DedicatedRepairEquipment";

  public static float getDaysToTrigger() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "DAYS_TO_TRIGGER");
    }
    return 0.3f;
  }

  public static float getRepairBonus() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "REPAIR_BONUS");
    }
    return 1.5f;
  }

  public static float getSuppliesRecoveryBonus() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "SUPPLIES_RECOVERY_BONUS");
    }
    return 0.5f;
  }

  public static float getUsageTax() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "USAGE_TAX");
    }
    return 1.2f;
  }

  public static boolean getSafeToDelete() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getBoolean(modId, "SAFE_DELETE");
    }
    return false;
  }

  public static String getCommodityUsed() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getString(modId, "COMMODITY_USED");
    }
    return Commodities.METALS;
  }

  public static float getConversionRatio() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      Float r = LunaSettings.getFloat(modId, "CONVERSION_RATIO");
      if (r != null) {
        return r;
      }
    }
    return MyMisc.getCommodityConversionRatio(Commodities.SUPPLIES, getCommodityUsed());
  }
}
