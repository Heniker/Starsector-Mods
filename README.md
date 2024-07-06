# Starsector Mods
Source code - https://github.com/Heniker/Starsector-Mods

### Build
Run `build/build.bash` file.

Javac and Jar installed by scoop:
```
scoop bucket add java
scoop install openjdk8-redhat
```

```bash
$ javac -version
javac 1.8.0_342
```

### Develop
During development mod should be placed in starsector mods folder.

For debugging add following to `vmparams` file in starsector directory:

```
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8015,suspend=n
```

Then launch VSCode "attach" debug configuration.

Sparse API reference can be found in `Starsector/starsector-core/starfarer.api.zip`

# Mods

### Improvised Refinery
- Improvised Refinery hullmod:<br/>
  Install an Improvised Refinery onto the ship. The Refinery can inefficently convert Ore into Metals.<br/>
  The conversion speed is based on CR and number of ships with hullmod in fleet. Having nanoforge in cargo also influences conversion speed and efficiency.<br/>
  A logistics hullmod. Can only be installed on Capital class ships. Unlocks when player gets Improvised Equipment skill. Can be randomly found in derelict structures.<br/>
  Updates (effectively) in real-time<br/>

- Toggle Refinery ability:<br/>
  Toggles Improvised Refinery hullmod for all ships within player fleet.<br/>

Safe to add/remove mid-game.

Similar'ish mods: 
- Aptly Simple Hullmods (Audax) - https://fractalsoftworks.com/forum/index.php?topic=24550.0
- Ore Refinery (Dazs) - https://fractalsoftworks.com/forum/index.php?topic=22882.0
- Supply Forging (Timid) - https://fractalsoftworks.com/forum/index.php?topic=17503.0

This mod is inspired by Supply Forging, but uses no code from Supply Forging and has a completely different implementation.

### Dedicated Repair Equipment
- Dedicated Repair Equipment hullmod:<br/>
  Ship with this hullmod can aid in repairs of a single other ship within fleet.<br/>
  Increases repair speed, but not CR recovery speed.<br/>
  While target is repairing/recovering CR - the recovery supplies cost will be reduced.<br/>
  Drains metals while active propotinal to target recovery cost.<br/>
  Can not be installed. Built-in on Salvage Rig.<br/>
  To prevent hullmod from activating - disable repairs on Salvage Rig or mothball it.

Safe to add/remove mid-game.

This idea came from old forum post mentioning how Salvage Rig used to work in the game. Also from the fact that Salvage Rig is called "Construction Rig" in the game files.

To modders:<br/>
If you feel like Dedicated Repair Equipment might fit in your ship design - adding it should be as simple as checking if mod is installed in ModPlugin and calling `ShipHullSpecAPI.addBuiltInMod`.
Everything else (like safe mid-game removal) should be handled by the mod itself.  Reach me out if it does not work that well.

<!--
Mod is 80% done;

TODO:
Test Luna integraion
See if mod can work without Luna
Test if mod can actually be removed mid-game
See if icon from ability can be deleted
Add version checker integration
Try to add conversionRate to Luna
add indicator for IR ability describing contributing factors (Nanoforge, participating ships)

Test nanoforge

Add icons for hullmods and ability
?Add post description for ImprovisedRefinery
?Add mod settings
Add detection range increase while refinery is active

TODONE:
Test SMod ratios.
check smod save on Ir 
Test Ore Conversion
Test multiple crigs in fleet
Add integration with Corrupted Nanoforge, Prestine Nanoforge.

---

Desc:
The mod adds 2 hullmods:
- Improvised Refinery:
  Install an Improvised Refinery onto the ship that can inefficently convert Ore into Metals.
  The conversion speed is based on CR and number of ships with hullmod in fleet.
  A logistics hullmod. Can only be installed on Capital class ships. Unlocks when player gets Improvised Equipment skill.

- Dedicated Repair Equipment
  Ship with this hullmod can aid in repairs of a single other ship within fleet.
  Increases repair speed, but not CR recovery speed.
  While target is repairing/recovering CR - the recovery supplies cost will be reduced.
  Drains metals while active propotinal to target recovery cost.
  Can not be installed. Built-in on Salvage Rig.

And a single ability:
- Toggle Ore Reginery:
  Toggles Improvised Refinery hullmod for all ships.

It's pretty much in beta state RN. The code seems to work fine from my testing, but I'll need to balance it a bit and add icons.
Btw, if any artists here feel like this is worth their time and want to draw some icons - don't hesitate to reach me out.
Balance suggestions and bug reports are very much welcome.
The source code is fully available. You should compile it yourself in case you don't trust the jar - script is `build/build.bash`. Use `git-bash` on windows, need `javac` and `jar`.
-->
