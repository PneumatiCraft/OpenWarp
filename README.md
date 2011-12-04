OpenWarp is a replacement for the Essentials player-movement functionality,
including `/warp`, `/home`, `/j`, and `/top`. It is meant to be used as
part of a suite of plugins for removing Essentials from a Minecraft server,
or as a standalone plugin to provide simple warp commands.

For more information, see [the wiki](https://github.com/PneumatiCraft/OpenWarp/wiki)

## Building

To build OpenWarp, you'll need a Java compiler and Maven 2.2 or later. Perform
the following:

1. Clone the OpenWarp Git repository:
   `git clone git://github.com/PneumatiCraft/OpenWarp.git`
2. In the cloned folder, update all submodules:
   `git submodule update --init --recursive`
3. Run Maven to build:
   `mvn clean package`

## Developing

OpenWarp welcomes contributions to the wiki, issue reports, and pull requests.
If you feel like hacking on OpenWarp, just fork the repo and start work, or
file an issue with your feature request and ideas for implementation.

If you do decide to code yourself, please remember that we try to adhere to 
Sun coding style (for the most part). You can use the Maven mojo 
`checkstyle:checkstyle` to check that your addition introduces no new style 
errors.

Part of proper style is proper Javadoc. You can generate the existing docs
with the mojo `javadoc:javadoc`, and it should be thorough enough to help
you in any code additions or modifications you may choose to make. Try to
keep it that way!

## License

Copyright (c) 2011, Tim Ekl
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
