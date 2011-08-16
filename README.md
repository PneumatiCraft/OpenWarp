OpenWarp is a replacement for the Essentials player-movement functionality,
including `/warp`, `/home`, `/j`, and `/top`. It is meant to be used as
part of a suite of plugins for removing Essentials from a Minecraft server,
or as a standalone plugin to provide simple warp commands.

For more information, see [the wiki](https://github.com/PneumatiCraft/OpenWarp/wiki)

## Building

To build OpenWarp, you'll need a Java compiler and Ant 1.8 or later. Perform
the following:

1. Clone the OpenWarp Git repository:
   `git clone git://github.com/PneumatiCraft/OpenWarp.git`
2. In the cloned folder, update all submodules:
   `git submodule update --init --recursive`
3. Build the `jar` target with Ant to generate the plugin JAR:
   `ant jar`
