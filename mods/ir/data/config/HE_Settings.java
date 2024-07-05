package mods.ir.data.config;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import lunalib.lunaSettings.LunaSettings;
import mods.common.MyMisc;

public class HE_Settings {
  static String modId = "HE_ImprovisedRefinery";

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
    return false;
  }

  static public float getConversionRatio() {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      Float r = LunaSettings.getFloat(modId, "CONVERSION_RATIO");
      if (r != null) {
        return r;
      }
    }
    return MyMisc.getCommodityConversionRatio(getCommodityFrom(), getCommodityTo());
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
    return 72f;
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
    return 0.75f;
  }
}
