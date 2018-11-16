package c4.champions.common.config;

import c4.champions.Champions;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Champions.MODID)
public class ConfigHandler {

    public static Client client = new Client();
    public static Growth growth = new Growth();

    public static class Growth {

        public double health = 0.35d;
        public double attackDamage = 0.5d;
        public double armor = 5.0d;
        public double armorToughness = 2.0d;
        public double knockbackResist = 0.2d;
        public double exp = 1;
    }

    public static class Client {

        public String[] colors = new String[]{};
        public boolean renderGUI = true;
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
