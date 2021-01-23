# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.16.5-2.0.1.0] - 2021.01.22
### Added
- Added Scaling Health integration
### Fixed
- Fixed CraftTweaker integration (note: this will break any current implementations)

## [1.16.4-2.0.0.2] - 2021.01.05
### Added
- Added Chinese localization (thanks EnterFor!)
### Changed
- Disabled Molten affix fire effects temporarily [#62](https://github.com/TheIllusiveC4/Champions/issues/62)
### Fixed
- Attempted fix for race condition crash [#69](https://github.com/TheIllusiveC4/Champions/issues/69)

## [1.16.3-2.0.0.1] - 2020.10.03
### Changed
- Updated to Minecraft 1.16.3

## [1.16.2-2.0.0.0] - 2020.09.09
### Changed
- Updated to Minecraft 1.16.2

## [2.0-beta6] - 2020.07.21
### Changed
- Ported to 1.16.1 Forge
### Removed
- [API] Removed IAffix#onKnockBack since Minecraft removed the entity information in the callback

## [2.0-beta5] - 2020.07.20
### Fixed
- Fixed bullet renderer colorizer for Arctic and Enkindling affixes

## [2.0-beta4] - 2020.07.11
### Fixed
- Fixed Magnetic affix pull vectors being too strong

## [2.0-beta3] - 2020.06.15
### Changed
- Ported to 1.15.2

## [2.0-beta2] - 2020.06.14
### Added
- Added Turkish translation (thanks Emirhangg!)

## [2.0-beta1] - 2020.05.31
### Added
- Added config options for Infested parasite mob type
- Added config options for HUD and particle effects
- Added preset affixes config option to ranks
- Added min and max tiers config option to affixes
- Added entities config options
### Changed
- Ported to 1.14.4 Forge
- Standardized affix names:
    - Cinder -> Enkindling
    - Desecrator -> Desecrating
    - Jailer -> Paralyzing
    - Knockback -> Knocking
    - Reflecting -> Reflective
    - Scrapper -> Wounding
    - Vortex -> Magnetic
- Standardized effect names:
    - Injured -> Wound
    - Jailed -> Paralysis
- Plagued now only spreads from the initial host and has a visible aura for its range
- Ranks are now only server-side
- Revamped champion names to use generic identifiers
### Removed
- Removed client config option for overriding rank colors
- Removed Plagued potion effect