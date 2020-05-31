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

package top.theillusivec4.champions;

import com.electronwill.nightconfig.core.CommentedConfig;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.champions.api.IChampionsApi;
import top.theillusivec4.champions.api.impl.ChampionsApiImpl;
import top.theillusivec4.champions.client.ClientEventHandler;
import top.theillusivec4.champions.client.affix.ClientAffixEventsHandler;
import top.theillusivec4.champions.client.config.ClientChampionsConfig;
import top.theillusivec4.champions.client.renderer.ChampionsRenderer;
import top.theillusivec4.champions.common.affix.core.AffixManager;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.loot.EntityIsChampion;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.util.EntityManager;
import top.theillusivec4.champions.server.command.ChampionsCommand;

@Mod(Champions.MODID)
public class Champions {

  public static final String MODID = "champions";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final IChampionsApi API = ChampionsApiImpl.getInstance();

  public Champions() {
    ModLoadingContext.get().registerConfig(Type.CLIENT, ClientChampionsConfig.CLIENT_SPEC);
    ModLoadingContext.get().registerConfig(Type.SERVER, ChampionsConfig.SERVER_SPEC);
    createServerConfig(ChampionsConfig.RANKS_SPEC, "ranks");
    createServerConfig(ChampionsConfig.AFFIXES_SPEC, "affixes");
    createServerConfig(ChampionsConfig.ENTITIES_SPEC, "entities");
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::config);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::postSetup);
    MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    ChampionCapability.register();
    NetworkHandler.register();
    AffixManager.register();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    MinecraftForge.EVENT_BUS.register(new ClientAffixEventsHandler());
    Minecraft.getInstance().getItemColors()
        .register(ChampionEggItem::getColor, ChampionsRegistry.EGG);
    ChampionsRenderer.register();
  }

  private void postSetup(final FMLLoadCompleteEvent evt) {
    DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior() {
      @Nonnull
      @Override
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        Direction direction = source.getBlockState().get(DispenserBlock.FACING);
        Optional<EntityType<?>> entitytype = ChampionEggItem.getType(stack);
        entitytype.ifPresent(type -> {
          Entity entity = type.create(source.getWorld(), stack.getTag(), null, null,
              source.getBlockPos().offset(direction), SpawnReason.DISPENSER, true,
              direction != Direction.UP);

          if (entity instanceof LivingEntity) {
            ChampionCapability.getCapability((LivingEntity) entity)
                .ifPresent(champion -> ChampionEggItem.read(champion, stack));
            source.getWorld().addEntity(entity);
            stack.shrink(1);
          }
        });
        return stack;
      }
    };
    DispenserBlock.registerDispenseBehavior(ChampionsRegistry.EGG, dispenseBehavior);
    LootConditionManager.registerCondition(new EntityIsChampion.Serializer());
  }

  private void serverStarting(final FMLServerStartingEvent evt) {
    ChampionsCommand.register(evt.getCommandDispatcher());
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MODID)) {

      if (evt.getConfig().getType() == Type.SERVER) {
        ChampionsConfig.bake();
        ForgeConfigSpec spec = evt.getConfig().getSpec();
        CommentedConfig commentedConfig = evt.getConfig().getConfigData();

        if (spec == ChampionsConfig.RANKS_SPEC) {
          ChampionsConfig.transformRanks(commentedConfig);
          RankManager.buildRanks();
        } else if (spec == ChampionsConfig.AFFIXES_SPEC) {
          ChampionsConfig.transformAffixes(commentedConfig);
          AffixManager.buildAffixSettings();
        } else if (spec == ChampionsConfig.ENTITIES_SPEC) {
          ChampionsConfig.transformEntities(commentedConfig);
          EntityManager.buildEntitySettings();
        }
      } else if (evt.getConfig().getType() == Type.CLIENT) {
        ClientChampionsConfig.bake();
      }
    }
  }

  private static void createServerConfig(ForgeConfigSpec spec, String suffix) {
    String fileName = "champions-" + suffix + ".toml";
    ModLoadingContext.get().registerConfig(Type.SERVER, spec, fileName);
    File defaults = new File(FMLPaths.GAMEDIR.get() + "/defaultconfigs/" + fileName);

    if (!defaults.exists()) {
      try {
        FileUtils.copyInputStreamToFile(
            Objects.requireNonNull(Champions.class.getClassLoader().getResourceAsStream(fileName)),
            defaults);
      } catch (IOException e) {
        LOGGER.error("Error creating default config for " + fileName);
      }
    }
  }
}
