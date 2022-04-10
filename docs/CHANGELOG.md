# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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
