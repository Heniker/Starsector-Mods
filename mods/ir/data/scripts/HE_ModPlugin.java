package mods.ir.data.scripts;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import mods.common.MyHullmodSaver;
import mods.ir.data.campaign.HE_AbilityToggle;
import mods.ir.data.config.HE_Settings;
import mods.ir.data.hullmods.HE_ImprovisedRefinery;

public class HE_ModPlugin extends BaseModPlugin {
  public static Logger log = Global.getLogger(HE_ModPlugin.class);

  public static final String ABILITY_ENABLED = "$" + HE_AbilityToggle.ID + ".isActive";

  @Override
  public void afterGameSave() {
    MemoryAPI persist = Global.getSector().getMemory();

    if (HE_Settings.getSafeToDelete()) {
      MyHullmodSaver.restoreModdedShips(HE_ImprovisedRefinery.ID);
    }

    try {
      if (HE_Settings.getUnlockAtStart()
          || Global.getSector().getPlayerStats().getSkillLevel("makeshift_equipment") >= 1) {
        Global.getSector().getCharacterData().addAbility(HE_AbilityToggle.ID);
        Global.getSector().getPlayerFleet().addAbility(HE_AbilityToggle.ID);

        Global.getSector().getPlayerFaction().addKnownHullMod(HE_ImprovisedRefinery.ID);
        Global.getSector().getCharacterData().addHullMod(HE_ImprovisedRefinery.ID);
      }
    } catch (NullPointerException err) {
    }

    try {
      if ((boolean) persist.get(ABILITY_ENABLED)) {
        Global.getSector().getPlayerFleet().getAbility(HE_AbilityToggle.ID).activate();
      }
    } catch (NullPointerException err) {
    }
  }

  @Override
  public void beforeGameSave() {
    MemoryAPI persist = Global.getSector().getMemory();

    try {
      persist.set(ABILITY_ENABLED,
          Global.getSector().getPlayerFleet().getAbility(HE_AbilityToggle.ID).isActive());
    } catch (NullPointerException err) {
    }

    if (HE_Settings.getSafeToDelete()) {
      MyHullmodSaver.saveDeleteModdedShips(HE_ImprovisedRefinery.ID, HE_ImprovisedRefinery.state.keySet());

      try {
        persist.set(ABILITY_ENABLED,
            Global.getSector().getPlayerFleet().getAbility(HE_AbilityToggle.ID).isActive());
        Global.getSector().getCharacterData().removeAbility(HE_AbilityToggle.ID);
        Global.getSector().getPlayerFleet().removeAbility(HE_AbilityToggle.ID);

        Global.getSector().getPlayerFaction().removeKnownHullMod(HE_ImprovisedRefinery.ID);
        Global.getSector().getCharacterData().removeHullMod(HE_ImprovisedRefinery.ID);
      } catch (NullPointerException err) {
      }
    } else {
      HE_Settings.updateSettings();
    }
  }

  @Override
  public void onGameLoad(boolean newGame) {
    this.afterGameSave();
  }

  @Override
  public void onApplicationLoad() throws Exception {
    if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
      new HE_InitLunaListener().init();
    }
  }
}
