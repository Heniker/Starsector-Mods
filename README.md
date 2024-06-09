## README

### Build
Run `build/build.bash` file.

During development mod should be placed in starsector mods folder.


### Develop
For debugging add following to `vmparams` file in starsector directory:

```
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8015,suspend=y
```

Then launch VSCode "attach" debug configuration.

Sparse API reference can be found in `Starsector/starsector-core/starfarer.api.zip`
