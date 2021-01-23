# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.0.11.10] - 2021.01.22
### Fixed
- Fixed Cinder Spark death message [#60](https://github.com/TheIllusiveC4/Champions/pull/60)

## [1.0.11.9] - 2020.07.20
### Added
- Added client config option for name renders [#53](https://github.com/TheIllusiveC4/Champions/issues/53)
- Chinese localization (thanks Minecraft-Prayer!)

## [1.0.11.8] - 2020.05.08
### Changed
- Plagued effect no longer infects armor stands [#45](https://github.com/TheIllusiveC4/Champions/issues/45)

## [1.0.11.7] - 2020.05.07
### Changed
- Plagued effect no longer spreads between non-source entities [#44](https://github.com/TheIllusiveC4/Champions/issues/44)

## [1.0.11.6] - 2020.05.03
### Fixed
- Fixed rank spawning issues with Champions Mob List configuration [#43](https://github.com/TheIllusiveC4/Champions/issues/43)

## [1.0.11.5] - 2020.05.01
### Changed
- Death messages will only activate from kills caused by players

## [1.0.11.4] - 2020.04.14
### Changed
- Added random tier range to Champion Mobs config option [#40](https://github.com/TheIllusiveC4/Champions/issues/40)
### Fixed
- Potentially fixed crashing due to infinite reflection recursion [#39](https://github.com/TheIllusiveC4/Champions/issues/39)
- Disabled affixes will now be removed from mobs upon restart

## [1.0.11.3] - 2019.12.24
### Changed
- Re-implemented Hide Effects config option to be client-sided

## [1.0.11.2] - 2019.12.10
### Added
- Added config option to hide champion effects [#36](https://github.com/TheIllusiveC4/Champions/issues/36)
- Added config option for Vortex affix strength
### Changed
- Increased strength of Vortex affix effect

## [1.0.11.1] - 2019.11.17
### Added
- Added config option to scale loot drop amount to Champion tier for Config Loot
- Added config option for Molten affix water resistance
### Fixed
- Fixed death message config option applying to tiers at and below the set amount instead of at and above

## [1.0.11] - 2019.11.09
### Added
- Added Cinder affix, champions will shoot flaming homing projectiles that do damage and set targets on fire [#28](https://github.com/TheIllusiveC4/Champions/issues/28)
- Added additional config options for Reflecting affix [#30](https://github.com/TheIllusiveC4/Champions/issues/30)
### Fixed
- Fixed crashes with name configs [#33](https://github.com/TheIllusiveC4/Champions/issues/33)

## [1.0.10.1] - 2019.07.18
### Changed
- Lifted restriction on /spawnchampion and /spawnchampion commands so that 
non-player entities can use them

## [1.0.10] - 2019.07.14
### Added
- [API] Added onHealed affix event
- Added /spawnchampionat command for summoning champions to specific coordinates
- Added specific tier functionality for champions mob list config option [#23](https://github.com/TheIllusiveC4/Champions/issues/23)

### Changed
- Changed Infested affix behavior to prevent infinite spawns, each champion now only holds a small, conditionally renewable amount of parasites [#22](https://github.com/TheIllusiveC4/Champions/issues/22)
- Removed limitations on affixes assigned to always be on entities, so they now ignore tier checks and validity checks [#23](https://github.com/TheIllusiveC4/Champions/issues/23)

### Fixed
- Fixed potential NPE in affix generation [#26](https://github.com/TheIllusiveC4/Champions/issues/26)

## [1.0.9] - 2019.04.24
### Added
- Config option for health bar visibility range [#21](https://github.com/TheIllusiveC4/Champions/issues/21)

### Changed
- Champion health bars can now be seen through transparent/partially transparent blocks such as glass [#21](https://github.com/TheIllusiveC4/Champions/issues/21)

### Fixed
- Fixed possible NPE [#24](https://github.com/TheIllusiveC4/Champions/issues/24)

## [1.0.8] - 2019.03.22
### Added
- Config option to stop loot drops when killed by fake players [#19](https://github.com/TheIllusiveC4/Champions/issues/19)

### Changed
- Default config value for Infected potion changed from Wither to Poison

## [1.0.7] - 2019.03.20
### Added
- Potions can now be added to ranks, see GitHub wiki for details
- Config option for X and Y-offsets for Champion health bars on the HUD [#18](https://github.com/TheIllusiveC4/Champions/issues/18)

## [1.0.6] - 2019.03.10
### Added
- Config option for Infested affix Silverfish on Damage Taken, was originally merged with Silverfish per Attack [#17](https://github.com/TheIllusiveC4/Champions/issues/17)

### Changed
- Infested affix will no longer spawn silverfish on damage taken unless that damage was from a living entity

## [1.0.5.2] - 2019.03.10
### Fixed
- Fixed Death Message Tier config option not applying correctly

## [1.0.5.1] - 2019.03.08
### Changed
- Aether Legacy's Sun Spirit is blacklisted by default due to erratic behavior from the AI when combined wth Champion traits [#16](https://github.com/TheIllusiveC4/Champions/issues/16)

### Fixed
- Fixed Scaling Health integration not applying difficulty modifiers to the first tier

## [1.0.5] - 2019.02.25
### Added
- Config options for Desecrator affix to control cloud wait time, radius, and duration [#14](https://github.com/TheIllusiveC4/Champions/issues/14)
- Creeper explosions increase in size by champion tier, configurable
- Config option to enable death messages for champion slaying [#15](https://github.com/TheIllusiveC4/Champions/issues/15)
- Configurable Scaling Health integration for increasing champion spawns by difficulty rating [#13](https://github.com/TheIllusiveC4/Champions/issues/13)

## [1.0.4.1] - 2019.01.19
### Changed
- Champion candidate selection will now catch more mobs (i.e. spawn eggs, Wither, Ender Dragon, unnatural spawns, etc.)
- Champion eggs will no longer be listed in JEI

## [1.0.4] - 2019.01.16
### Added
- Ender variants of Infested affix that will spawn Endermites instead of Silverfish

## [1.0.3.1] - 2019.01.16
### Fixed
- Client-side exceptions related to networking errors [#10](https://github.com/TheIllusiveC4/Champions/issues/10)
- Particle aura colors from champions not being overridden on the client

## [1.0.3] - 2019.01.12
### Added
- Config option for mobs that will always spawn as champions

### Changed
- Optimizations for beacon checks when using the beacon config option, should drastically reduce performance issues

### Fixed
- Out of bounds error related to the Infested affix [#8](https://github.com/TheIllusiveC4/Champions/issues/8)

## [1.0.2] - 2019.01.05
### Added
- Champions will once again not spawn within a configurable range of active beacons [#7](https://github.com/TheIllusiveC4/Champions/issues/7)
- More Plagued affix config options to control infection behavior (Infection Potion, Infection Power, Infection Duration, Infection Range)

## [1.0.1.1] - 2019.01.02
### Fixed
- Missing lang entry for Reflecting death message [#6](https://github.com/TheIllusiveC4/Champions/issues/6)

## [1.0.1] - 2019.01.02
### Changed
- Reflecting damage now uses its own identifier [#6](https://github.com/TheIllusiveC4/Champions/issues/6)

## [1.0] - 2018.12.30
### Added
- GameStages integration for tiered stages

### Changed
- GameStages integration now checks players within 256 blocks instead of 128 blocks

### Removed
- Champions not spawning when within 24 blocks of an active beacon

## [0.11] - 2018.12.30
### Added
- GameStages integration, documentation coming soon to GitHub
- Config option to add more possible names/name suffixes for champions

## [0.10] - 2018.12.26
### Fixed
- Dedicated server crash [#5](https://github.com/TheIllusiveC4/Champions/issues/5)

## [0.9] - 2018.12.26
### Added
- Knockback affix - Increases knockback from attacks, which will also slow targets for a short amount of time
- Affix configs for Desecrator, Infested, Jailer, Knockback, Plagued, Reflecting, Scrapper
- /championegg command that creates an egg of a specific type of champion [#4](https://github.com/TheIllusiveC4/Champions/issues/4)

### Removed
- Horde affix

## [0.8] - 2018.12.15
### Fixed
- Dedicated server crash

## [0.7] - 2018.12.15
### Fixed
- Particle bugs causing crashes

## [0.6] - 2018.11.29
### Added
- Scrapper affix - Champion attacks will have a moderate chance to inflict the Injured effect, which decreases healing and increases damage taken
- Config options for Adaptable, Arctic, and Dampening affixes

## [0.5] - 2018.11.28
### Added
- Adaptable affix - Champion will take less and less damage from the same consecutive damage type
- Config option to control whether champions can spawn from mob spawners [#3](https://github.com/TheIllusiveC4/Champions/issues/3)
- Config option to set movement speed bonus for Hasty affix

### Changed
- Hasty affix movement speed bonus has been lowered by half
- Plague effect now also drains hunger/adds exhaustion

### Fixed
- /spawnchampion command not working in instances where the command sender was not a player

## [0.4] - 2018.11.26
### Added
- Config options for certain affix settings, currently there are settings for the Horde and Lively affixes
- Loot weight as a parameter to the loot config option
- Config option for target ability range

### Changed
- Desecrator and Vortex now require line of sight before activating
- Lively regeneration will now pause for a short time when attacked, this cooldown is configurable in the configs
- Jailer can no longer spawn on Creepers
- Vortex will activate slightly more often, but is much weaker now
- Plagued now checks for line of sight before infecting entities
- Horde minions will now stay close to the champion and attack whatever the champion attacks

### Fixed
- Horde minions spawning far from the champion

## [0.3] - 2018.11.23
### Added
- Plagued affix - Infects anything nearby with Plague, which adds a Wither effect to the target and the target becomes a new Plague host
- Configuration option for loot drops, now able to choose between using the loot tables, the configuration file, or both to determine champion drops

### Changed
- [API] EntityHasTier property changed to EntityIsChampion, refer to Github wiki for documentation

### Fixed
- Cascading worldgen lag due to beacon checks (and possible the Horde affix)
- Jailed potion icon

## [0.2] - 2018.11.21
### Added
- Lively affix - Mob will passively regenerate 1 health per second. Regeneration increases to 5 health per second when not actively targeting an enemy.
- More available names for champions
- Mob blacklist/whitelist config option for champion eligibility
- Colored stars above champion health bar to denote tier of champion
- Command to summon specific champions, "/spawnchampion"

### Fixed
- Loot incompatibility when using LootTweaker

## [0.1] - 2018.11.19
Initial beta release