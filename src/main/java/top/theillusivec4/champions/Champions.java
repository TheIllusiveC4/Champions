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

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.RankManager;

@Mod(Champions.MODID)
public class Champions {

  public static final String MODID = "champions";
  public static final Logger LOGGER = LogManager.getLogger();

  public Champions() {
    ModLoadingContext.get()
        .registerConfig(Type.SERVER, ChampionsConfig.RANKS_SPEC, "champions-ranks.toml");
    File defaultRanks = new File(FMLPaths.GAMEDIR.get() + "/defaultconfigs/champions-ranks.toml");

    if (!defaultRanks.exists()) {
      try {
        FileUtils.copyInputStreamToFile(Objects.requireNonNull(
            Champions.class.getClassLoader().getResourceAsStream("champions-ranks.toml")),
            defaultRanks);
      } catch (IOException e) {
        LOGGER.error("Error creating default ranks config!");
      }
    }
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::config);
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MODID)) {

      if (evt.getConfig().getSpec() == ChampionsConfig.RANKS_SPEC) {
        ChampionsConfig.transform(evt.getConfig().getConfigData());
        RankManager.buildRanks();
      }
    }
  }
}
