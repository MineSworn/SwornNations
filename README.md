SwornNations 
====================
Guilding and user-controlled antigrief plugin for Minecraft. Modified for MineSworn.
This plugin will allow the players on the server to create factions/guilds. The factions can claim territory that will be protected from non-members. Factions can forge alliances and declare themselves enemies with others. Land may be taken from other factions through war.

The goals of this plugin:

 * The players should be able to take care of anti-griefing themselves.
 * Inspire politics and intrigues on your server.
 * Guilding and team spirit! :)

SwornNations:
The goal of the SwornFactions fork is to add extra functionality to the
original plugin and continue support of the 1.6 branch of Factions for future
Minecraft updates.

Compiling
---------

SwornNations is compiled using [Maven](https://maven.apache.org/) and Java 8. To compile, run `mvn install`.

Usage
---------

The chat console command is:

 * `/f`

This command has subcommands like:

* `/f create MyFactionName`
* `/f invite MyFriendsName`
* `/f claim`
* `/f map`
* ... etc

You may also read the documentation ingame as the plugin ships with an ingame help manual. Read the help pages like this:

* `/f help 1`
* `/f help 2`
* `/f help 3`

Note that you may optionally skip the slash and just write

* `f`

A default config file will be created on the first run.

License
----------
This project has a LGPL license just like the Bukkit project.<br>
This project uses [GSON](http://code.google.com/p/google-gson/) which has a [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0 ).

