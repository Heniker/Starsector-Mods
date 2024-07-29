package mods.dre.data.config;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import lunalib.lunaSettings.LunaSettings;
import mods.dre.Constants;
import mods.dre.data.hullmods.HE_DedicatedRepairEquipment;

public class HE_Settings {
  public static float getDaysToTrigger() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(Constants.MOD_ID, "DAYS_TO_TRIGGER");
    }
    return 0.3f;
  }

  public static float getRepairBonus() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(Constants.MOD_ID, "REPAIR_BONUS");
    }
    return 1.5f;
  }

  public static float getSuppliesRecoveryBonus() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(Constants.MOD_ID, "SUPPLIES_RECOVERY_BONUS");
    }
    return 0.5f;
  }

  public static float getUsageTax() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(Constants.MOD_ID, "USAGE_TAX");
    }
    return 1.2f;
  }

  public static boolean getSafeToDelete() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getBoolean(Constants.MOD_ID, "SAFE_DELETE");
    }
    return true;
  }

  public static String getCommodityUsed() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getString(Constants.MOD_ID, "COMMODITY_USED");
    }
    return Commodities.METALS;
  }

  public static void updateSettings() {
    HE_DedicatedRepairEquipment.DAYS_TO_TRIGGER = getDaysToTrigger();
    HE_DedicatedRepairEquipment.REPAIR_BONUS = getRepairBonus();
    HE_DedicatedRepairEquipment.SUPPLIES_RECOVERY_BONUS = getSuppliesRecoveryBonus();
    HE_DedicatedRepairEquipment.COMMODITY_USED = getCommodityUsed();
    HE_DedicatedRepairEquipment.USAGE_TAX = getUsageTax();
  }
}
