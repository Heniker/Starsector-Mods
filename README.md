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

<!--
Mod is 80% done;

TODO:
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
