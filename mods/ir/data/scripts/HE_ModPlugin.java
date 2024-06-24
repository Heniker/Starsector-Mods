package mods.ir.data.scripts;

import java.util.HashSet;
import org.apache.log4j.Logger;

import java.util.Set;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.thoughtworks.xstream.XStream;

import mods.ir.data.campaign.HE_AbilityToggle;
import mods.ir.data.hullmods.HE_ImprovisedRefinery;

@SuppressWarnings("unchecked")
public class HE_ModPlugin extends BaseModPlugin {
    public static Logger log = Global.getLogger(HE_ModPlugin.class);

    public static final String ABILITY_ENABLED = "$" + HE_AbilityToggle.ID + ".isActive";
    public static final String REFINERY_SHIPS = "$" + HE_ImprovisedRefinery.ID + ".ships";

    @Override
    public void afterGameSave() {
        MemoryAPI persist = Global.getSector().getMemory();

        try {
            for (FleetMemberAPI it : (Set<FleetMemberAPI>) persist.get(REFINERY_SHIPS)) {
                if (it != null && it.getVariant() != null) {
                    it.getVariant().addMod(HE_ImprovisedRefinery.ID);
                }
            }
        } catch (NullPointerException err) {
        }

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
        MemoryAPI persist = Global.getSector().getMemory();

        persist.unset(REFINERY_SHIPS);
        // this prevernts the game from saving excess information
        persist.set(REFINERY_SHIPS, new HashSet(HE_ImprovisedRefinery.state.keySet()));

        for (FleetMemberAPI it : HE_ImprovisedRefinery.state.keySet()) {
            it.getVariant().getHullMods().remove(HE_ImprovisedRefinery.ID);
            it.getVariant().removeMod(HE_ImprovisedRefinery.ID);
            it.getVariant().removePermaMod(HE_ImprovisedRefinery.ID);
        }

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

    @Override
    public void configureXStream(XStream x) {
    }
}
