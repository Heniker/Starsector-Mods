package mods.dre.data.scripts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import java.util.Set;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.thoughtworks.xstream.XStream;

import mods.dre.data.hullmods.HE_DedicatedRepairEquipment;
import mods.dre.data.hullmods.HE_DedicatedRepairEquipment.RepairEquipmentBuff;
import mods.dre.data.hullmods.HE_DedicatedRepairEquipment.State;

@SuppressWarnings("unchecked")
public class HE_ModPlugin extends BaseModPlugin {
    public static Logger log = Global.getLogger(HE_ModPlugin.class);

    public static final String REPAIR_EQUIPMENT_SHIPS = "$" + HE_DedicatedRepairEquipment.ID + ".ships";

    @Override
    public void afterGameSave() {
        MemoryAPI persist = Global.getSector().getMemory();

        try {
            for (FleetMemberAPI it : (Set<FleetMemberAPI>) persist.get(REPAIR_EQUIPMENT_SHIPS)) {
                try {
                    it.getHullSpec().getBuiltInMods().add(HE_DedicatedRepairEquipment.ID);
                    it.getVariant().addPermaMod(HE_DedicatedRepairEquipment.ID);
                } catch (NullPointerException err) {
                }
            }
        } catch (NullPointerException err) {
        }
    }

    @Override
    public void beforeGameSave() {
        MemoryAPI persist = Global.getSector().getMemory();

        persist.unset(REPAIR_EQUIPMENT_SHIPS);
        // this prevernts the game from saving excess information
        persist.set(REPAIR_EQUIPMENT_SHIPS, new HashSet(HE_DedicatedRepairEquipment.state.keySet()));

        for (FleetMemberAPI it : HE_DedicatedRepairEquipment.state.keySet()) {
            try {
                it.getHullSpec().getBuiltInMods().remove(HE_DedicatedRepairEquipment.ID);
                it.getVariant().getHullMods().remove(HE_DedicatedRepairEquipment.ID);
                it.getVariant().removeMod(HE_DedicatedRepairEquipment.ID);
                it.getVariant().removePermaMod(HE_DedicatedRepairEquipment.ID);
            } catch (NullPointerException err) {
            }

            try {
                HE_DedicatedRepairEquipment.state.get(it).repairTarget.getBuffManager()
                        .removeBuff(HE_DedicatedRepairEquipment.RepairEquipmentBuff.BUFF_ID);
            } catch (NullPointerException err) {
            }
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
