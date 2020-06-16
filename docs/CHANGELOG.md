# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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