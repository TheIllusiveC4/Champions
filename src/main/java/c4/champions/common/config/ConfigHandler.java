/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.config;

import c4.champions.Champions;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Champions.MODID)
public class ConfigHandler {

    @Name("Champions from Spawners")
    @Comment("Set whether champions can spawn from mob spawners")
    public static boolean championSpawners = false;

    @Name("Dimension Permission Mode")
    @Comment("Set whether the dimension configuration is blacklisted or whitelisted")
    @RequiresMcRestart
    public static PermissionMode dimensionPermission = PermissionMode.BLACKLIST;

    @Name("Dimension List")
    @Comment("Set which dimensions, listed as number ids, are blacklisted or whitelisted for champion mobs, leave " +
            "blank to disable this option")
    @RequiresMcRestart
    public static String[] dimensionList = new String[]{};

    @Name("Mob Permission Mode")
    @Comment("Set whether the mob configuration is blacklisted or whitelisted")
    @RequiresMcRestart
    public static PermissionMode mobPermission = PermissionMode.BLACKLIST;

    @Name("Mob List")
    @Comment("Set which mobs are blacklisted or whitelisted for champion mobs, leave blank to disable this option")
    @RequiresMcRestart
    public static String[] mobList = new String[]{};

    @Name("Champion Mobs List")
    @Comment("List of mobs that will always spawn as champions")
    @RequiresMcRestart
    public static String[] championsList = new String[]{};

    @Name("Loot Source")
    @Comment("Set whether champion mobs drop loot from the loot table, config, or both")
    public static LootSource lootSource = LootSource.LOOT_TABLE;

    @Name("Loot Drops")
    @Comment("Sets the loot drops from champions if loot source is set to CONFIG, format is tier;modid:name;metadata;" +
            "stacksize;enchant(true/false);weight")
    @RequiresMcRestart
    public static String[] lootDrops = new String[]{};

    @Name("Additional Champion Names")
    @Comment("Additional names that will be added to the pool of names given to champions")
    public static String[] championNames = new String[]{};

    @Name("Additional Champion Name Suffixes")
    @Comment("Additional name suffixes that will be added to the pool of names given to champions")
    public static String[] championNameSuffixes = new String[]{};

    @Name("Beacon Blacklist Range")
    @Comment("The range an active beacon will prevent champion spawns, 0 to disable")
    public static int beaconRange = 64;

    @Name("Death Message Tier")
    @Comment("The tier (and above) of champions that will have death messages sent out upon defeat, 0 to disable")
    public static int deathMessageTier = 0;

    @Name("Client Settings")
    @Comment("Settings that are only client-side")
    public static Client client = new Client();

    @Name("Growth Settings")
    @Comment("Settings that affect the growth rate of champion mobs")
    public static Growth growth = new Growth();

    @Name("Affix Settings")
    @Comment("Settings that affect individual affixes")
    public static Affix affix = new Affix();

    @Name("Scaling Health Integration")
    @Comment("Settings for integration with the Scaling Health mod")
    public static ScalingHealth scalingHealth = new ScalingHealth();

    public static class Affix {

        @Name("Maximum Ability Range")
        @Comment("Set the maximum distance that mobs can use their targeted abilities from, 0 to disable")
        public int abilityRange = 0;

        @Name("Adaptable")
        @Comment("Settings for the Adaptable affix")
        public Adaptable adaptable = new Adaptable();

        @Name("Arctic")
        @Comment("Settings for the Arctic affix")
        public Arctic arctic = new Arctic();

        @Name("Dampening")
        @Comment("Settings for the Dampening affix")
        public Dampening dampening = new Dampening();

        @Name("Desecrator")
        @Comment("Settings for the Desecrator affix")
        public Desecrator desecrator = new Desecrator();

        @Name("Hasty")
        @Comment("Settings for the Hasty affix")
        public Hasty hasty = new Hasty();

        @Name("Infested")
        @Comment("Settings for the Infested affix")
        public Infested infested = new Infested();

        @Name("Jailer")
        @Comment("Settings for the Jailer affix")
        public Jailer jailer = new Jailer();

        @Name("Knockback")
        @Comment("Settings for the Knockback affix")
        public Knockback knockback = new Knockback();

        @Name("Lively")
        @Comment("Settings for the Lively affix")
        public Lively lively = new Lively();

        @Name("Plagued")
        @Comment("Settings for the Plagued affix")
        public Plagued plagued = new Plagued();

        @Name("Reflecting")
        @Comment("Settings for the Reflecting affix")
        public Reflecting reflecting = new Reflecting();

        @Name("Scrapper")
        @Comment("Settings for the Scrapper affix")
        public Scrapper scrapper = new Scrapper();

        public class Adaptable {

            @Name("Damage Reduction Increment")
            @Comment("The increase in damage reduction for each consecutive attack of the same damage type")
            public double damageReductionIncrement = 0.15d;

            @Name("Maximum Damage Reduction")
            @Comment("The maximum damage reduction")
            public double maxDamageReduction = 0.9d;
        }

        public class Arctic {

            @Name("Attack Interval")
            @Comment("How often the champion will shoot projectiles (in ticks)")
            public int attackInterval = 20;
        }

        public class Dampening {

            @Name("Damage Reduction")
            @Comment("The amount of damage reduction to apply to indirect attacks")
            public double damageReduction = 0.8d;
        }

        public class Desecrator {

            @Name("Attack Interval")
            @Comment("How often the champion will create harming clouds (in ticks)")
            public int attackInterval = 60;

            @Name("Cloud Activation Time")
            @Comment("How long, in ticks, it takes for the effect cloud to activate after being placed")
            public int activationTicks = 20;

            @Name("Cloud Radius")
            @Comment("The radius of the cloud effect")
            public double cloudRadius = 4.0f;

            @Name("Cloud Duration")
            @Comment("The duration, in ticks, of the cloud effect")
            public int cloudDuration = 200;
        }

        public class Hasty {

            @Name("Movement Bonus")
            @Comment("The base movement speed bonus")
            public double movementBonus = 0.25d;
        }

        public class Infested {

            @Name("Silverfish per Attack")
            @Comment("Number of silverfish to spawn per attack")
            public int silverfishPerAttack = 1;

            @Name("Silverfish on Death per Tier")
            @Comment("Average number of silverfish to spawn on death per tier")
            public int silverfishOnDeath = 4;
        }

        public class Jailer {

            @Name("Chance per Attack")
            @Comment("The percent chance that an attack will jail targets")
            public double chance = 0.2d;
        }

        public class Knockback {

            @Name("Knockback Multiplier")
            @Comment("The multiplier to apply to the knockback strength")
            public double multiplier = 5.0d;
        }

        public class Lively {

            @Name("Heal Amount")
            @Comment("The amount of health per second regeneration")
            public double healAmount = 1.0d;

            @Name("Passive Multiplier")
            @Comment("Multiplier to health regeneration when not aggressive")
            public double passiveMultiplier = 5.0d;

            @Name("Cooldown on Attacked")
            @Comment("Set cooldown (in seconds) for regeneration after getting attacked")
            public int cooldown = 3;
        }

        public class Plagued {

            @Name("Plague Duration")
            @Comment("The duration (in ticks) of the plague effect")
            public int duration = 300;

            @Name("Infection Potion")
            @Comment("The potion that will be spread through the plague effect")
            @RequiresMcRestart
            public String infectPotion = "minecraft:wither";

            @Name("Infection Potion Duration")
            @Comment("The duration (in ticks) of the infection potion")
            public int infectDuration = 200;

            @Name("Infection Potion Power")
            @Comment("The power of the infection potion (base: 1)")
            public int infectPower = 1;

            @Name("Infection Range")
            @Comment("The range that the infection will spread from hosts")
            public int infectRange = 3;
        }

        public class Reflecting {

            @Name("Minimum Percent of Damage")
            @Comment("The minimum percent of damage to reflect back")
            public double minimumPerc = 0.1d;

            @Name("Maximum Percent of Damage")
            @Comment("The maximum percent of damage to reflect back")
            public double maximumPerc = 0.35d;
        }

        public class Scrapper {

            @Name("Chance per Attack")
            @Comment("The percent chance that an attack will injure targets")
            public double chance = 0.4d;
        }
    }

    public static class Growth {

        @Name("Health")
        @Comment("The percent increase in health multiplied by the growth factor")
        public double health = 0.35d;

        @Name("Attack Damage")
        @Comment("The percent increase in attack damage multiplied by the growth factor")
        public double attackDamage = 0.5d;

        @Name("Armor")
        @Comment("The increase in armor multiplied by the growth factor")
        public double armor = 2.0d;

        @Name("Armor Toughness")
        @Comment("The increase in armor toughness multiplied by the growth factor")
        public double armorToughness = 1.0d;

        @Name("Knockback Resistance")
        @Comment("The increase in knockback resistance multiplied by the growth factor")
        public double knockbackResist = 0.05d;

        @Name("Experience")
        @Comment("The increase in experience multiplied by the growth factor")
        public double exp = 1;

        @Name("Creeper Explosion Strength")
        @Comment("The increase in creeper explosion strength multiplied by tier, NOT by growth factor")
        public int creeperStrength = 2;
    }

    public static class Client {

        @Name("Rank Colors")
        @Comment("A list of colors, as numbers, for each rank")
        public String[] colors = new String[]{};
    }

    public static class ScalingHealth {

        @Name("Tier Spawn Modifiers")
        @Comment("List of tiers with numbers to multiply spawn rates by difficulty (i.e. '1;0.02' to increase tier 1 spawns by 2 percent per difficulty)." +
                " Note that tier spawn chances are cumulative, so increasing lower tier spawns will naturally increase higher tier spawns.")
        @RequiresMcRestart
        public String[] spawnModifiers = new String[]{};
    }

    public enum PermissionMode {
        BLACKLIST,
        WHITELIST;
    }

    public enum LootSource {
        LOOT_TABLE,
        CONFIG,
        BOTH
    }

    @Mod.EventBusSubscriber(modid = Champions.MODID)
    private static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
            if (evt.getModID().equals(Champions.MODID)) {
                ConfigManager.sync(Champions.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
