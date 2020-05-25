package top.theillusivec4.champions.common.affix.core;

import net.minecraft.nbt.CompoundNBT;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IChampion;

public abstract class AffixData {

  private IChampion champion;
  private String identifier;

  protected AffixData() {}

  public void readData(IChampion champion, String identifier) {
    this.champion = champion;
    this.identifier = identifier;
    readFromNBT(champion.getServer().getData(identifier));
  }

  public abstract void readFromNBT(CompoundNBT tag);

  public abstract CompoundNBT writeToNBT();

  public void saveData() {
    champion.getServer().setData(identifier, writeToNBT());
  }

  public static <T extends AffixData> T getData(IChampion champion, String id, Class<T> clazz) {
    T data = null;

    try {
      data = clazz.newInstance();
      data.readData(champion, id);
    } catch (IllegalAccessException | InstantiationException e) {
      Champions.LOGGER.error("Error reading data from class " + clazz.toString());
    }
    return data;
  }

  public static class BooleanData extends AffixData {

    public boolean mode;

    @Override
    public void readFromNBT(CompoundNBT tag) {
      mode = tag.getBoolean("mode");
    }

    @Override
    public CompoundNBT writeToNBT() {
      CompoundNBT compound = new CompoundNBT();
      compound.putBoolean("mode", mode);
      return compound;
    }
  }

  public static class IntegerData extends AffixData {

    public int num;

    @Override
    public void readFromNBT(CompoundNBT tag) {
      num = tag.getInt("num");
    }

    @Override
    public CompoundNBT writeToNBT() {
      CompoundNBT compound = new CompoundNBT();
      compound.putInt("num", num);
      return compound;
    }
  }
}
