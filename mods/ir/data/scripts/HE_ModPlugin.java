package mods.ir.data.scripts;

import java.util.HashSet;
import org.apache.log4j.Logger;

import java.util.Set;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import mods.common.MyHullmodSaver;
import mods.ir.data.campaign.HE_AbilityToggle;
import mods.ir.data.config.HE_Settings;
import mods.ir.data.hullmods.HE_ImprovisedRefinery;

public class HE_ModPlugin extends BaseModPlugin {
    public static Logger log = Global.getLogger(HE_ModPlugin.class);

    public static final String ABILITY_ENABLED = "$" + HE_AbilityToggle.ID + ".isActive";

    @Override
    public void afterGameSave() {
        if (!HE_Settings.getSafeToDelete()) {
            return;
        }

        MemoryAPI persist = Global.getSector().getMemory();

        MyHullmodSaver.restoreModdedShips(HE_ImprovisedRefinery.ID);

        try {
            if (Global.getSector().getPlayerStats().getSkillLevel("makeshift_equipment") >= 1) {
                Global.getSector().getCharacterData().addAbility(HE_AbilityToggle.ID);
                Global.getSector().getPlayerFleet().addAbility(HE_AbilityToggle.ID);

                Global.getSector().getPlayerFaction().addKnownHullMod(HE_ImprovisedRefinery.ID);
                Global.getSector().getCharacterData().addHullMod(HE_ImprovisedRefinery.ID);
            }

            if ((boolean) persist.get(ABILITY_ENABLED)) {
                Global.getSector().getPlayerFleet().getAbility(HE_AbilityToggle.ID).activate();
            }

        } catch (NullPointerException err) {
        }
    }

    @Override
    public void beforeGameSave() {
        if (!HE_Settings.getSafeToDelete()) {
            return;
        }

        MemoryAPI persist = Global.getSector().getMemory();

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
    }

    @Override
    public void onGameLoad(boolean newGame) {
        this.afterGameSave();
    }
}
