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

    @Name("Dimension Permission Mode")
    @Comment("Set whether the dimension configuration is blacklisted or whitelisted")
    @RequiresMcRestart
    public static PermissionMode dimensionPermission = PermissionMode.BLACKLIST;

    @Name("Dimension List")
    @Comment("Set which dimensions, listed as number ids, are blacklisted or whitelisted for champion mobs, leave " +
            "blank to disable this option")
    @RequiresMcRestart
    public static String[] dimensionList = new String[]{};

    @Name("Client Settings")
    @Comment("Settings that are only client-side")
    public static Client client = new Client();

    @Name("Growth Settings")
    @Comment("Settings that affect the growth rate of champion mobs")
    public static Growth growth = new Growth();

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
