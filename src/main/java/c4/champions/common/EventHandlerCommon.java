package c4.champions.common;

import c4.champions.Champions;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class EventHandlerCommon {

    @SubscribeEvent
    public void livingDrops(LivingDropsEvent evt) {
        EntityLivingBase entity = evt.getEntityLiving();

        if (ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);

            if (chp != null && ChampionHelper.isElite(chp.getRank())) {

                if (entity.world instanceof WorldServer) {
                    WorldServer world = (WorldServer) entity.world;
                    LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation
                            (Champions.MODID, "ranked_mobs"));
                    DamageSource source = evt.getSource();
                    LootContext.Builder builder = new LootContext.Builder(world).withDamageSource(evt.getSource()).withLootedEntity(entity);

                    if (source.getTrueSource() instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) source.getTrueSource();
                        builder.withPlayer(player).withLuck(player.getLuck());
                    }
                    LootContext ctx = builder.build();
                    List<ItemStack> stacks = table.generateLootForPools(world.rand, ctx);

                    for (ItemStack stack : stacks) {
                        EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
                                stack);
                        entityitem.setDefaultPickupDelay();
                        evt.getDrops().add(entityitem);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void livingXP(LivingExperienceDropEvent evt) {
        EntityLivingBase entity = evt.getEntityLiving();

        if (ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);

            if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                evt.setDroppedExperience((int)(chp.getRank().getGrowthFactor() * ConfigHandler.growth.exp * evt
                        .getOriginalExperience()));
            }
        }
    }
}
