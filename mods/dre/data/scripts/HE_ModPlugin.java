package mods.dre.data.scripts;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import mods.common.MyHullmodSaver;
import mods.dre.Constants;
import mods.dre.data.config.HE_Settings;
import mods.dre.data.hullmods.HE_DedicatedRepairEquipment;
import mods.dre.data.hullmods.HE_DedicatedRepairEquipment.State;

public class HE_ModPlugin extends BaseModPlugin {
    public static final Logger log = Global.getLogger(HE_ModPlugin.class);

    @Override
    public void afterGameSave() {
        if (!HE_Settings.getSafeToDelete()) {
            return;
        }

        MyHullmodSaver.restoreModdedShips(HE_DedicatedRepairEquipment.ID);
    }

    @Override
    public void beforeGameSave() {
        if (!HE_Settings.getSafeToDelete()) {
            return;
        }

        MyHullmodSaver.saveDeleteModdedShips(HE_DedicatedRepairEquipment.ID,
                HE_DedicatedRepairEquipment.state.keySet());

        for (State it : HE_DedicatedRepairEquipment.state.values()) {
            if (it == null || it.repairTarget == null || it.repairTarget.getBuffManager() == null) {
                continue;
            }

            it.repairTarget.getBuffManager()
                    .removeBuff(HE_DedicatedRepairEquipment.BUFF_ID);
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        HE_Settings.updateSettings();
        this.afterGameSave();
    }

    public class SettingsListener implements LunaSettingsListener {
        @Override
        public void settingsChanged(String modId) {
            if (modId != Constants.MOD_ID) {
                return;
            }

            HE_Settings.updateSettings();
        }
    }

    @Override
    public void onApplicationLoad() throws Exception {
        LunaSettings.addSettingsListener(new SettingsListener());
    }
}
