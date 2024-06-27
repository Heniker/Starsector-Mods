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
    public static final String REFINERY_S_MOD_SHIPS = "$" + HE_ImprovisedRefinery.ID + ".smodships";

    @Override
    public void afterGameSave() {
        MemoryAPI persist = Global.getSector().getMemory();

        try {
            for (FleetMemberAPI it : (Set<FleetMemberAPI>) persist.get(REFINERY_SHIPS)) {
                try {
                    it.getVariant().addMod(HE_ImprovisedRefinery.ID);
                } catch (NullPointerException err) {
                }
            }

            for (FleetMemberAPI it : (Set<FleetMemberAPI>) persist.get(REFINERY_S_MOD_SHIPS)) {
                try {
                    it.getVariant().addPermaMod(HE_ImprovisedRefinery.ID, true);
                } catch (NullPointerException err) {
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
        persist.unset(REFINERY_S_MOD_SHIPS);

        HashSet<FleetMemberAPI> refineryShips = new HashSet<FleetMemberAPI>();
        HashSet<FleetMemberAPI> refinerySModShips = new HashSet<FleetMemberAPI>();
        for (FleetMemberAPI it : HE_ImprovisedRefinery.state.keySet()) {
            try {
                if (it.getVariant().getPermaMods().contains(HE_ImprovisedRefinery.ID)) {
                    refinerySModShips.add(it);
                } else {
                    refineryShips.add(it);
                }
            } catch (NullPointerException err) {
            }

            try {
                it.getVariant().getHullMods().remove(HE_ImprovisedRefinery.ID);
                it.getVariant().removeMod(HE_ImprovisedRefinery.ID);
                it.getVariant().removePermaMod(HE_ImprovisedRefinery.ID);
            } catch (NullPointerException err) {
            }
        }

        persist.set(REFINERY_SHIPS, refineryShips);
        persist.set(REFINERY_S_MOD_SHIPS, refinerySModShips);

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
