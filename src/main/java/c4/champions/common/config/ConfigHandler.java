package c4.champions.common.config;

import c4.champions.Champions;
import net.minecraftforge.common.config.Config;

@Config(modid = Champions.MODID)
public class ConfigHandler {

    public static Growth growth = new Growth();

    public static class Growth {

        public double health = 0.35d;
        public double attackDamage = 0.5d;
        public double movementSpeed = 0.1d;
        public double armor = 5.0d;
        public double armorToughness = 2.0d;
        public double knockbackResist = 0.2d;
        public double exp = 1;
    }
}
