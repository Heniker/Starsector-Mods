package mods.dre.data.scripts;

import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import mods.dre.data.config.HE_Settings;

/**
 * Different file to leave Luna as optional dependency. Construct this after
 * checking if Luna is installed.
 */
public class HE_InitLunaListener {
  class SettingsListener implements LunaSettingsListener {
    @Override
    public void settingsChanged(String modId) {
      HE_Settings.updateSettings();
    }
  }

  public void init() {
    LunaSettings.addSettingsListener(new SettingsListener());
  }
}
