# Starsector Mods
Source code - https://github.com/Heniker/Starsector-Mods

### Build
Run `build/build.bash` file.

Requirements installed by scoop:
```
scoop install git 7zip
scoop bucket add java
scoop install openjdk8-redhat
```

```bash
$ javac -version
javac 1.8.0_342
```

### Develop
During development repo should be placed in starsector mods folder.

For debugging add following to `vmparams` file in starsector directory:

```
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8015,suspend=n
```

Then launch VSCode "attach" debug configuration.

Sparse API reference can be found in `Starsector/starsector-core/starfarer.api.zip`

# Mods

### Improvised Refinery
- Improvised Refinery hullmod:<br/>
  Install an Improvised Refinery onto the ship. The Refinery can inefficiently convert Ore into Metals.<br/>
  The conversion speed is based on CR and number of ships with hullmod within fleet. Having nanoforge in cargo also influences conversion speed and efficiency.<br/>
  A logistics hullmod. Can only be installed on Capital class ships. Unlocks when player gets Makeshift Equipment skill. Can be randomly found in derelict structures.<br/>
  Updates (effectively) in real-time.<br/>

- Toggle Refinery ability:<br/>
  Toggles Improvised Refinery hullmod for all ships within player fleet.<br/>

Safe to add/remove mid-game.

Similar'ish mods: 
- Aptly Simple Hullmods (Audax) - https://fractalsoftworks.com/forum/index.php?topic=24550.0
- Ore Refinery (Dazs) - https://fractalsoftworks.com/forum/index.php?topic=22882.0
- Supply Forging (Timid) - https://fractalsoftworks.com/forum/index.php?topic=17503.0<br/>
  This mod is somewhat inspired by Supply Forging, but uses no code from Supply Forging and has a completely different implementation.
- prv Starworks (prav) - https://fractalsoftworks.com/forum/index.php?topic=12553.0<br/>
  Rust Belt faction has Arc Smelter hullmod which does essentially the same thing.

### Dedicated Repair Equipment
- Dedicated Repair Equipment hullmod:<br/>
  Ship with this hullmod can aid in repairs of a single other ship within fleet.<br/>
  Increases repair speed, but not CR recovery speed.<br/>
  While target is repairing/recovering CR - the recovery supplies cost will be reduced.<br/>
  Drains metals while active proportional to target recovery cost.<br/>
  Can not be installed. Built-in on Salvage Rig.<br/>
  To prevent hullmod from activating - disable repairs on Salvage Rig or mothball it.

Safe to add/remove mid-game.

This idea came from old forum post mentioning how Salvage Rig used to work in the game. Also from the fact that Salvage Rig is called "Construction Rig" in the game files.

<!--
Mod is 96% done;

TODO:
Add version checker integration
add indicator for IR ability describing contributing factors (Nanoforge, participating ships)

Add icons for hullmods and ability
? Add post description for ImprovisedRefinery
? Add detection range increase while refinery is active

TODONE:
Try to add conversionRate to Luna
+- Test Luna integraion
Test nanoforge
Add mod settings
See if mod can work without Luna
Test if mod can actually be removed mid-game
Test SMod ratios.
check smod save on Ir 
Test Ore Conversion
Test multiple crigs in fleet
Add integration with Corrupted Nanoforge, Prestine Nanoforge.
-->
