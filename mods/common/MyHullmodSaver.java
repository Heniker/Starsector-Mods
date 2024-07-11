package mods.common;

import java.util.HashSet;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

@SuppressWarnings("unchecked")
public class MyHullmodSaver {

  private static enum StoreKeys {
    fleet,
    faction,
    supermod,
    standardmod,
    suppressedmod,
    builtin,
  }

  private static String getStoreKey(StoreKeys id, String modId) {
    switch (id) {
      case fleet: {
        return "$" + modId + ".modified-fleet";
      }
      case faction: {
        return "$" + modId + ".modified-faction";
      }
      case supermod: {
        return "$" + modId + ".modified-sModMember";
      }
      case standardmod: {
        return "$" + modId + ".modified-standardModMember";
      }
      case suppressedmod: {
        return "$" + modId + ".modified-suppressedModMember";
      }
      case builtin: {
        return "$" + modId + ".modified-hullSpec";
      }
      default: {
        throw new Error();
      }
    }
  }

  public static void restoreModdedShips(String modId) {
    MemoryAPI persist = Global.getSector().getMemory();

    HashSet<String> moddedFleets = (HashSet<String>) persist.get(getStoreKey(StoreKeys.fleet, modId));
    HashSet<String> moddedFactions = (HashSet<String>) persist.get(getStoreKey(StoreKeys.faction, modId));
    HashSet<String> supprsessedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.suppressedmod, modId));
    HashSet<String> moddedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.standardmod, modId));
    HashSet<String> sModdedShips = (HashSet<String>) persist.get(getStoreKey(StoreKeys.supermod, modId));
    HashSet<String> builtinSpecs = (HashSet<String>) persist.get(getStoreKey(StoreKeys.builtin, modId));

    if (moddedFleets == null || moddedShips == null || sModdedShips == null || supprsessedShips == null
        || moddedFactions == null || builtinSpecs == null) {
      return;
    }

    for (String it : moddedFactions) {
      for (FactionAPI that : Global.getSector().getAllFactions()) {
        if (that == null || that.getId() != it) {
          continue;
        }

        that.addKnownHullMod(modId);
      }
    }

    for (String it : builtinSpecs) {
      ShipHullSpecAPI spec = Global.getSettings().getHullSpec(it);
      if (spec == null) {
        continue;
      }

      spec.getBuiltInMods().add(modId);
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
        if (supprsessedShips.contains(id)) {
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
    HashSet<String> moddedFactions = new HashSet<String>();
    HashSet<String> suppressed = new HashSet<String>();
    HashSet<String> moddedShips = new HashSet<String>();
    HashSet<String> sModdedShips = new HashSet<String>();
    HashSet<String> builtinSpecs = new HashSet<String>();

    for (FactionAPI it : Global.getSector().getAllFactions()) {
      if (it == null || !it.knowsHullMod(modId)) {
        continue;
      }

      moddedFactions.add(it.getId());
      it.removeKnownHullMod(modId);
    }

    HashSet<ShipHullSpecAPI> modifiedHullSpecs = new HashSet<ShipHullSpecAPI>();

    for (FleetMemberAPI it : members) {
      if (it == null || it.getVariant() == null || it.getHullSpec() == null) {
        continue;
      }

      if (it.getVariant().getHullMods().contains(modId) && it.getFleetData() != null
          && it.getFleetData().getFleet() != null) {
        moddedFleets.add(it.getFleetData().getFleet().getId());
      }

      if (it.getHullSpec().getBuiltInMods().contains(modId)) {
        modifiedHullSpecs.add(it.getHullSpec());
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

    for (ShipHullSpecAPI it : modifiedHullSpecs) {
      builtinSpecs.add(it.getHullId());
      it.getBuiltInMods().remove(modId);
    }

    persist.unset(getStoreKey(StoreKeys.fleet, modId));
    persist.unset(getStoreKey(StoreKeys.faction, modId));
    persist.unset(getStoreKey(StoreKeys.suppressedmod, modId));
    persist.unset(getStoreKey(StoreKeys.standardmod, modId));
    persist.unset(getStoreKey(StoreKeys.supermod, modId));
    persist.unset(getStoreKey(StoreKeys.builtin, modId));

    persist.set(getStoreKey(StoreKeys.fleet, modId), moddedFleets);
    persist.set(getStoreKey(StoreKeys.faction, modId), moddedFactions);
    persist.set(getStoreKey(StoreKeys.suppressedmod, modId), suppressed);
    persist.set(getStoreKey(StoreKeys.standardmod, modId), moddedShips);
    persist.set(getStoreKey(StoreKeys.supermod, modId), sModdedShips);
    persist.set(getStoreKey(StoreKeys.builtin, modId), builtinSpecs);
  }
}
