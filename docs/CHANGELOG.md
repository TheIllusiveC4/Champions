# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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