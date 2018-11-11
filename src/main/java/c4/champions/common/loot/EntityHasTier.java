package c4.champions.common.loot;

import c4.champions.Champions;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;

import javax.annotation.Nonnull;
import java.util.Random;

public class EntityHasTier implements EntityProperty {

    private final int tier;

    public EntityHasTier(int tierIn)
    {
        this.tier = tierIn;
    }

    public boolean testProperty(@Nonnull Random random, @Nonnull Entity entityIn) {

        if (ChampionHelper.isValidChampion(entityIn)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityIn);
            return chp != null && ChampionHelper.isElite(chp.getRank()) && chp.getRank().getTier() >= tier;
        }
        return false;
    }

    public static class Serializer extends EntityProperty.Serializer<EntityHasTier> {

        public Serializer() {
            super(new ResourceLocation(Champions.MODID, "tier"), EntityHasTier.class);
        }

        @Nonnull
        public JsonElement serialize(@Nonnull EntityHasTier property, @Nonnull JsonSerializationContext serializationContext) {
            return new JsonPrimitive(property.tier);
        }

        @Nonnull
        public EntityHasTier deserialize(@Nonnull JsonElement element, @Nonnull JsonDeserializationContext
                deserializationContext) {
            return new EntityHasTier(JsonUtils.getInt(element, "tier"));
        }
    }
}