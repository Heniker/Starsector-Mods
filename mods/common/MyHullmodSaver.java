package mods.common;

import java.util.HashSet;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

@SuppressWarnings("unchecked")
public class MyHullmodSaver {

  private static enum StoreKeys {
    fleet,
    smod,
    mod,
    suppressedmod,
    builtin,
  }

  private static String getStoreKey(StoreKeys id, String modId) {
    switch (id) {
      case fleet: {
        return "$" + modId + ".moddedFleet";
      }
      case smod: {
        return "$" + modId + ".sModMember";
      }
      case mod: {
        return "$" + modId + ".standardModMember";
      }
      case suppressedmod: {
        return "$" + modId + ".suppressedModModMember";
      }
      case builtin: {
        return "$" + modId + ".suppressedModModMember";
      }
      default: {
        throw new Error();
      }
    }
  }

  public static void restoreModdedShips(String modId) {
    MemoryAPI persist = Global.getSector().getMemory();

    HashSet<String> moddedFleets = (HashSet<String>) persist.get(getStoreKey(StoreKeys.fleet, modId));
    HashSet<String> supprsessedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.suppressedmod, modId));
    HashSet<String> moddedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.mod, modId));
    HashSet<String> sModdedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.smod, modId));
    HashSet<String> builtinMods = (HashSet<String>) persist.get(getStoreKey(StoreKeys.builtin, modId));

    if (moddedFleets == null || moddedShips == null || sModdedShips == null || supprsessedShips == null) {
      return;
    }

    for (String it : moddedFleets) {
      SectorEntityToken entity = Global.getSector().getEntityById(it);
      CampaignFleetAPI fleet = (CampaignFleetAPI) (entity instanceof CampaignFleetAPI ? entity : null);
      if (fleet == null || fleet.getFleetData() == null || fleet.getFleetData().getMembersInPriorityOrder() == null) {
        continue;
      }

      for (FleetMemberAPI that : fleet.getFleetData().getMembersInPriorityOrder()) {
        if (that == null) {
          continue;
        }

        String id = that.getId();
        if (sModdedShips.contains(id) && builtinMods.contains(id)) {
          that.getHullSpec().addBuiltInMod(modId);
          that.getHullSpec().getBuiltInMods().add(modId); // idk why
          that.getVariant().addPermaMod(modId, true);
        } else if (builtinMods.contains(id)) {
          that.getHullSpec().addBuiltInMod(modId);
          that.getHullSpec().getBuiltInMods().add(modId); // idk why
          that.getVariant().addPermaMod(modId);
        } else if (supprsessedShips.contains(id)) {
          that.getVariant().addSuppressedMod(modId);
        } else if (sModdedShips.contains(id)) {
          that.getVariant().addPermaMod(modId, true);
        } else if (moddedShips.contains(id)) {
          that.getVariant().addMod(modId);
        }
      }
    }
  }

  public static void saveDeleteModdedShips(String modId, Set<FleetMemberAPI> members) {
    if (members == null) {
      return;
    }

    MemoryAPI persist = Global.getSector().getMemory();

    HashSet<String> moddedFleets = new HashSet<String>();
    HashSet<String> suppressed = new HashSet<String>();
    HashSet<String> moddedShips = new HashSet<String>();
    HashSet<String> sModdedShips = new HashSet<String>();
    HashSet<String> builtinMods = new HashSet<String>();

    HashSet<ShipHullSpecAPI> cleanupHullSpecs = new HashSet<ShipHullSpecAPI>();

    for (FleetMemberAPI it : members) {
      if (it == null || it.getVariant() == null || it.getHullSpec() == null) {
        continue;
      }

      if (it.getVariant().getHullMods().contains(modId) && it.getFleetData() != null
          && it.getFleetData().getFleet() != null) {
        moddedFleets.add(it.getFleetData().getFleet().getId());
      }

      if (it.getHullSpec().getBuiltInMods().contains(modId)) {
        builtinMods.add(it.getId());
        cleanupHullSpecs.add(it.getHullSpec());
      }

      if (it.getVariant().getSuppressedMods().contains(modId)) {
        suppressed.add(it.getId());
      } else if (it.getVariant().getSMods().contains(modId)) {
        sModdedShips.add(it.getId());
      } else if (it.getVariant().getHullMods().contains(modId)) {
        moddedShips.add(it.getId());
      }

      it.getVariant().getHullMods().remove(modId);
      it.getVariant().removeMod(modId);
      it.getVariant().removePermaMod(modId);
    }

    for (ShipHullSpecAPI it : cleanupHullSpecs) {
      it.getBuiltInMods().remove(modId);
    }

    persist.unset(getStoreKey(StoreKeys.fleet, modId));
    persist.unset(getStoreKey(StoreKeys.suppressedmod, modId));
    persist.unset(getStoreKey(StoreKeys.mod, modId));
    persist.unset(getStoreKey(StoreKeys.smod, modId));
    persist.unset(getStoreKey(StoreKeys.builtin, modId));

    persist.set(getStoreKey(StoreKeys.fleet, modId), moddedFleets);
    persist.set(getStoreKey(StoreKeys.suppressedmod, modId), suppressed);
    persist.set(getStoreKey(StoreKeys.mod, modId), moddedShips);
    persist.set(getStoreKey(StoreKeys.smod, modId), sModdedShips);
    persist.set(getStoreKey(StoreKeys.builtin, modId), builtinMods);
  }
}
