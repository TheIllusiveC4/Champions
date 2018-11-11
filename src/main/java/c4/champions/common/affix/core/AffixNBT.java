package c4.champions.common.affix.core;

import c4.champions.Champions;
import c4.champions.common.capability.IChampionship;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public abstract class AffixNBT {

    private IChampionship championship;
    private String identifier;

    private AffixNBT() {}

    public void readData(IChampionship championship, String identifier) {
        this.championship = championship;
        this.identifier = identifier;
        readFromNBT(championship.getAffixData(identifier));
    }

    public abstract void readFromNBT(NBTTagCompound tag);

    public abstract NBTTagCompound writeToNBT();

    public void saveData() {
        championship.setAffixData(identifier, writeToNBT());
    }

    public static <T extends AffixNBT> T getData(@Nonnull IChampionship championship, String affix, Class<T> clazz) {
        T data = null;

        try {
            data = clazz.newInstance();
            data.readData(championship, affix);
        } catch (IllegalAccessException | InstantiationException e) {
            Champions.logger.log(Level.ERROR, "Error reading data from class " + clazz.toString());
        }
        return data;
    }

    public static class Boolean extends AffixNBT {

        public boolean mode;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            mode = tag.getBoolean("mode");
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("mode", mode);
            return compound;
        }
    }
}
