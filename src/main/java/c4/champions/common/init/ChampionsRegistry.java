package c4.champions.common.init;

import c4.champions.Champions;
import c4.champions.common.entity.EntityJail;
import c4.champions.common.potion.PotionJailed;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Champions.MODID)
@Mod.EventBusSubscriber(modid = Champions.MODID)
public class ChampionsRegistry {

    @GameRegistry.ObjectHolder("jailed")
    public static final Potion jailed = null;

    @SubscribeEvent
    public static void registerPotion(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(new PotionJailed());
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> evt) {
        int id = 1;
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityJail.class)
                .id(new ResourceLocation(Champions.MODID, "entity_jail"), id++)
                .name(Champions.MODID + ".entity_jail")
                .tracker(256, 20, false)
                .build();
        evt.getRegistry().register(entry);
    }
}
