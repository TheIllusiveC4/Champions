# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.18.2-2.1.6.3] - 2023.01.04
### Fixed
- Fixed Champion summoning through NBT tags not working with certain spawner configurations

## [1.18.2-2.1.6.2] - 2022.12.25
### Fixed
- Fixed Champions health bars rendering multiple times per frame [#146](https://github.com/TheIllusiveC4/Champions/issues/146)

## [1.18.2-2.1.6.1] - 2022.11.02
### Added
- Added Champion summoning through NBT tags [#138](https://github.com/TheIllusiveC4/Champions/issues/138)
### Fixed
- Fixed performance issues with beacon checks [#140](https://github.com/TheIllusiveC4/Champions/issues/140)
- Fixed crash related to beacon checks [#142](https://github.com/TheIllusiveC4/Champions/issues/142)

## [1.18.2-2.1.6.0] - 2022.10.08
### Added
- Added Game Stages integration through the new `champions-gamestages.toml` configuration file [#135](https://github.com/TheIllusiveC4/Champions/issues/135)
### Changed
- Updated Russian `ru_ru` localization (thanks MiniRaptor!) [#127](https://github.com/TheIllusiveC4/Champions/pull/127)
- Changed Reflective affix's reflected damage to list the champion as the source rather than the player to avoid recursive [#133](https://github.com/TheIllusiveC4/Champions/issues/133)
player effects
### Fixed
- Fixed experience drop growth from champions stacking incorrectly with other mods [#134](https://github.com/TheIllusiveC4/Champions/issues/134)
- Fixed crash with Scaling Health 6.3.0 or above [#131](https://github.com/TheIllusiveC4/Champions/issues/131)
- Fixed champions ignoring configured beacon protection range [#130](https://github.com/TheIllusiveC4/Champions/issues/130)
- Fixed champions HUD disappearing or desyncing after player death [#129](https://github.com/TheIllusiveC4/Champions/issues/129)
- Fixed potential race condition crash with configuration files [#126](https://github.com/TheIllusiveC4/Champions/issues/126)

## [1.18.2-2.1.5.6] - 2022.06.20
### Fixed
- Fixed Global Loot Modifiers running twice when generating champion loot [#125](https://github.com/TheIllusiveC4/Champions/issues/125)

## [1.18.2-2.1.5.5] - 2022.06.13
### Fixed
- Fixed NPE crash with rank configuration [#122](https://github.com/TheIllusiveC4/Champions/issues/122)
- Fixed deadlock with Scaling Health integration [#121](https://github.com/TheIllusiveC4/Champions/issues/121) [#124](https://github.com/TheIllusiveC4/Champions/issues/124)

## [1.18.2-2.1.5.4] - 2022.05.08
### Changed
- "Champion Mobs Killed" stat has been reset to address a potential crash
### Fixed
- Fixed crash when using "Champion Mobs Killed" stat with FTBQuests [#120](https://github.com/TheIllusiveC4/Champions/issues/120)

## [1.18.2-2.1.5.3] - 2022.04.25
### Fixed
- Fixed dedicated server crash with Jade [#119](https://github.com/TheIllusiveC4/Champions/issues/119)

## [1.18.2-2.1.5.2] - 2022.04.24
### Changed
- Updated `zh_cn` translation (thanks WakelessSloth56!) [#118](https://github.com/TheIllusiveC4/Champions/pull/118)
### Fixed
- Fixed blacklist/whitelist configuration option using incorrect entity keys for entries

## [1.18.2-2.1.5.1] - 2022.04.20
### Fixed
- Fixed `ko_kr` localization

## [1.18.2-2.1.5.0] - 2022.04.19
### Added
- Added "Champion Mobs Killed" to stats screen
- Added `champions` entity selector option when using commands
### Changed
- Updated `ko_kr` localization (thanks PixVoxel!) [#116](https://github.com/TheIllusiveC4/Champions/pull/116)
### Fixed
- Fixed `champions:champion_properties` loot condition potentially applying to non-champion entities
- Fixed Shielding affix preventing Creative damage

## [1.18.2-2.1.4.0] - 2022.04.17
### Added
- Added integration with WAILA-based mods: Jade and WTHIT

## [1.18.2-2.1.3.0] - 2022.04.16
### Added
- Added "Champion Hunter" advancement for killing any champion mob
- Added revamped loot condition, `champions:champion_properties`, that can be used with advancement triggers [#114](https://github.com/TheIllusiveC4/Champions/pull/114)
- Added integration with TheOneProbe [#115](https://github.com/TheIllusiveC4/Champions/pull/115)
### Fixed
- Fixed converted entities losing their champion abilities [#113](https://github.com/TheIllusiveC4/Champions/issues/113)

## [1.18.2-2.1.2.3] - 2022.04.09
### Changed
- `chance` fields in `champions-ranks.toml` now accepts `0.0` as a valid value

## [1.18.2-2.1.2.2] - 2022.03.29
### Fixed
- Fixed `death.attack.cinderBullet` death messages lacking localization [#112](https://github.com/TheIllusiveC4/Champions/issues/112)

## [1.18.2-2.1.2.1] - 2022.03.19
### Fixed
- Fixed backwards compatibility with mods that have Champions integration for 1.18.2-2.1.1.0 and below

## [1.18.2-2.1.2.0] - 2022.03.19
### Changed
- Champion abilities now update server-side once every 10 ticks instead of every tick, increasing performance on servers
### Fixed
- Fixed Minecraft 1.18.1 backwards compatibility, again
- Fixed boss bars overlapping with Champions bars [#110](https://github.com/TheIllusiveC4/Champions/issues/110)
- Fixed Hasty affix not being applied correctly

## [1.18.2-2.1.1.0] - 2022.03.15
### Added
- Added syncing to client-side behavior for affixes
### Changed
- Small tweaks to increase performance
### Fixed
- Fixed shadows missing from text on the Champions entity HUD
- Fixed bullet rendering for Enkindling and Arctic affixes

## [1.18.2-2.1.0.1] - 2022.03.13
### Fixed
- Fixed Minecraft 1.18.1 backwards compatibility

## [1.18.2-2.1.0.0] - 2022.03.13
### Added
- Added `ko_kr.json` localization (thanks PixVoxel!) [#100](https://github.com/TheIllusiveC4/Champions/pull/100)
- Added `ru_ru.json` localization (thanks MiniRaptor!) [#107](https://github.com/TheIllusiveC4/Champions/pull/107)
### Changed
- Updated to Minecraft 1.18.2 (thanks Raycoms!) [#109](https://github.com/TheIllusiveC4/Champions/pull/109)
- `/champions` commands now include affix suggestions
### Fixed
- Fixed Hasty affix not persisting
