# ImprovisedRefinery
> Hullmod that converts Ore into Rare Ore

### Build
Run `build/build.bash` file.

Javac and Jar installed by scoop:
`scoop install openjdk8-redhat`

```bash
$ javac -version
javac 1.8.0_342
```

### Develop
During development mod should be placed in starsector mods folder.

For debugging add following to `vmparams` file in starsector directory:

```
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8015,suspend=y
```

Then launch VSCode "attach" debug configuration.

Sparse API reference can be found in `Starsector/starsector-core/starfarer.api.zip`

<!--
TODO:
Test SMod ratios.
Add integration with Corrupted Nanoforge, Prestine Nanoforge.
-->
