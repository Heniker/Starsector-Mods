package mods.ir.data.config;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import lunalib.lunaSettings.LunaSettings;
import mods.ir.data.hullmods.HE_ImprovisedRefinery;

public class HE_Settings {
  static String modId = "HE_ImprovisedRefinery";
  public static Logger log = Global.getLogger(HE_Settings.class);

  static public float getDaysToTrigger() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "DAYS_TO_TRIGGER");
    }
    return 0.3f;
  }

  static public boolean getSafeToDelete() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getBoolean(modId, "SAFE_DELETE");
    }
    return true;
  }

  static public String getCommodityFrom() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getString(modId, "COMMODITY_FROM");
    }
    return Commodities.ORE;
  }

  static public String getCommodityTo() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getString(modId, "COMMODITY_TO");
    }
    return Commodities.METALS;
  }

  static public float getNanoforgeBonusRate() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "NANOFORGE_BONUS_RATE");
    }
    return 1.5f;
  }

  static public float getSmodBonusRate() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "SMOD_BONUS_RATE");
    }
    return 1.4f;
  }

  static public float getBaseConversionSpeed() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "BASE_CONVERSION_SPEED");
    }
    return 65f;
  }

  static public float getPristineNanoforgeTaxBonus() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "PRISTINE_N_TAX_BONUS_FLAT");
    }
    return 0.1f;
  }

  static public float getConversionTax() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      return LunaSettings.getFloat(modId, "CONVERSION_TAX");
    }
    return 0.70f;
  }

  // not auto updated
  static public boolean getUnlockAtStart() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      String option = LunaSettings.getString(modId, "UNLOCK_AT_START");
      if (option.equals("auto")) {
        return Global.getSettings().getModManager().isModEnabled("second_in_command");
      }
      if (option.equals("true")) {
        return true;
      }
      if (option.equals("false")) {
        return false;
      }
      return false;
    }
    return false;
  }

  static public void updateSettings() {
    HE_ImprovisedRefinery.DAYS_TO_TRIGGER = getDaysToTrigger();
    HE_ImprovisedRefinery.COMMODITY_FROM = getCommodityFrom();
    HE_ImprovisedRefinery.COMMODITY_TO = getCommodityTo();
    HE_ImprovisedRefinery.CONVERSION_TAX = getConversionTax();
    HE_ImprovisedRefinery.PRISTINE_N_TAX_BONUS_FLAT = getPristineNanoforgeTaxBonus();
    HE_ImprovisedRefinery.BASE_CONVERSION_SPEED = getBaseConversionSpeed();
    HE_ImprovisedRefinery.SMOD_BONUS_RATE = getSmodBonusRate();
    HE_ImprovisedRefinery.NANOFORGE_BONUS_RATE = getNanoforgeBonusRate();
  }
}
