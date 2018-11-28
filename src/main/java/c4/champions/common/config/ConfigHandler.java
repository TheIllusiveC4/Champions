/*
 * Copyright (C) 2018  C4
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

    @Name("Loot Source")
    @Comment("Set whether champion mobs drop loot from the loot table, config, or both")
    public static LootSource lootSource = LootSource.LOOT_TABLE;

    @Name("Loot Drops")
    @Comment("Sets the loot drops from champions if loot source is set to CONFIG, format is tier;modid:name;metadata;" +
            "stacksize;enchant(true/false);weight")
    @RequiresMcRestart
    public static String[] lootDrops = new String[]{};

    @Name("Client Settings")
    @Comment("Settings that are only client-side")
    public static Client client = new Client();

    @Name("Growth Settings")
    @Comment("Settings that affect the growth rate of champion mobs")
    public static Growth growth = new Growth();

    @Name("Affix Settings")
    @Comment("Settings that affect individual affixes")
    public static Affix affix = new Affix();

    public static class Affix {

        @Name("Maximum Ability Range")
        @Comment("Set the maximum distance that mobs can use their targeted abilities from, 0 to disable")
        public int abilityRange = 0;

        @Name("Horde")
        @Comment("Settings for the Horde affix")
        public Horde horde = new Horde();

        @Name("Lively")
        @Comment("Settings for the Lively affix")
        public Lively lively = new Lively();

        public class Horde {

            @Name("Base Additional Mobs")
            @Comment("The base number of mobs to spawn with the champion")
            public int hordeSize = 2;

            @Name("Tier Multiplier")
            @Comment("The number to multiply by the tier to add to the base number to spawn")
            public int multiplier = 2;
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
    }

    public static class Client {

        @Name("Rank Colors")
        @Comment("A list of colors, as numbers, for each rank")
        public String[] colors = new String[]{};
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
