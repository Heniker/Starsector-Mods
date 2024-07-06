package mods.dre.data.config;

import lunalib.lunaSettings.LunaSettingsListener;
import mods.dre.Constants;

public class HE_SettingsListener implements LunaSettingsListener {

  @Override
  public void settingsChanged(String modId) {
    if (modId != Constants.MOD_ID) {
      return;
    }

    HE_Settings.updateSettings();
  }
}