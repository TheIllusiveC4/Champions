# Champions [![](http://cf.way2muchnoise.eu/versions/champions.svg)](https://minecraft.curseforge.com/projects/champions) [![](http://cf.way2muchnoise.eu/short_champions_downloads.svg)](https://minecraft.curseforge.com/projects/champions/files) [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

Champions is a mod that adds elite mobs of different rarities, with enhanced stats, extra abilities, and extra loot. These champions can be spawned for any hostile mob in the game. Many aspects of this mod can be configured with either the config file or the generated json files, allowing users to adjust the difficulty and diversity of this mod to suit their particular playstyle.

## Ranks

Champion mobs are defined by their ranks. Every time a valid mob is spawned into the world, it's assigned a rank. Most mobs will be common and exhibit no special characteristics. Some mobs will spawn as champions. Players can recognize them by the special colored particles they emit as well as the special health bar that appears on your screen when you look at them. Champions have ranks which are randomly assigned with higher ranks being progressively rarer. With each rank, champions gain stronger stats, more abilities, and more loot to drop.

Ranks are extremely configurable through the use of the **ranks.json** file generated in the **config/champions** folder. Players can edit rank attributes such as default color, number of affixes, growth factor, and chance to spawn. New ranks can even be added, infinitely.

## Affixes

Affixes are unique abilities or attributes that spawn with champion mobs. Players can see which affixes are attached to any champion by looking underneath the champion's health bar on the screen. These can be identified from a fair distance away, allowing players to sufficiently analyze each champion's threat level and available abilities.

Affixes, like ranks, can also be configured through the use of the **affixes.json** file generated in the **config/champions** folder. Here, players can disable certain affixes, require certain ranks, blacklist entities, and make specific entities always spawn with certain affixes if possible. Unlike ranks, new affixes cannot be added in any way.

Current available affixes:

* **Arctic** - Continuously fires homing projectiles that will slow anyone they hit
* **Dampening** - Reduces the damage of indirect attacks
* **Desecrator** - Periodically spawns a cloud of harming underneath its target
* **Hasty** - Drastically increases movement speed
* **Horde** - Spawns with several additional mobs of its type (these mobs are always Common)
* **Infested** - Attacking and being attacked will spawn silverfish that will attack its target
* **Jailer** - Small chance per attack to "jail" targets, making them unable to move positions for a few seconds
* **Lively** - Regenerates 1 health per second. This increases to 5 health per second when not attacking or pursuing any targets.
* **Molten** - Grants fire resistance, fiery attacks, and armor penetration
* **Plagued** - Infects nearby creatures with Plague, which adds a Wither effect and makes the creature a new Plague host
* **Reflecting** - A small portion of damage is reflected back at the attacker
* **Shielding** - Periodically shields itself from all damage
* **Vortex** - Periodically pulls targets towards itself

## Configuration

In addition to the json files mentioned above, Champions has a general configuration file. Here, players can specify things like dimensional blacklists/whitelists for champion mobs, client color overrides for ranks, and values to use for stat growth for champions.


