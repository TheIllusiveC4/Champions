package top.theillusivec4.champions.common.capability;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.rank.Rank;

public class ChampionCapability {

  @CapabilityInject(IChampion.class)
  public static final Capability<IChampion> CHAMPION_CAP;

  public static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "champion");

  static {
    CHAMPION_CAP = null;
  }

  public interface IChampion {

    Rank getRank();

    void setRank();
  }
}
