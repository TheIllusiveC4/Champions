package top.theillusivec4.champions.common.affix;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class AdaptableAffix extends BasicAffix {

  public AdaptableAffix() {
    super("adaptable", AffixCategory.DEFENSE);
  }

  @Override
  public float onHurt(IChampion champion, DamageSource source, float amount, float newAmount) {
    String type = source.getDamageType();
    DamageData damageData = AffixData.getData(champion, this.getIdentifier(), DamageData.class);

    if (damageData.name.equalsIgnoreCase(type)) {
      newAmount -= amount * ChampionsConfig.adaptableDamageReductionIncrement * damageData.count;
      damageData.count++;
    } else {
      damageData.name = type;
      damageData.count = 0;
    }
    damageData.saveData();
    return Math
        .max(amount * (float) (1.0f - ChampionsConfig.adaptableMaxDamageReduction), newAmount);
  }

  public static class DamageData extends AffixData {

    String name;
    int count;

    @Override
    public void readFromNBT(CompoundNBT tag) {
      name = tag.getString("name");
      count = tag.getInt("count");
    }

    @Override
    public CompoundNBT writeToNBT() {
      CompoundNBT compound = new CompoundNBT();
      compound.putString("name", name);
      compound.putInt("count", count);
      return compound;
    }
  }
}
