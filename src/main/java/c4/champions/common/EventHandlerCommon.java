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

package c4.champions.common;

import c4.champions.Champions;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class EventHandlerCommon {

    private static final ResourceLocation CHAMPION_LOOT = new ResourceLocation(Champions.MODID, "champion_loot");

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent evt) {
        EntityLivingBase entityLivingBase = evt.getEntityLiving();
        boolean flag = entityLivingBase.world.getGameRules().getBoolean("showDeathMessages");
        if (flag && !entityLivingBase.world.isRemote && ChampionHelper.isValidChampion(entityLivingBase)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityLivingBase);

            if (chp != null && ChampionHelper.isElite(chp.getRank()) && chp.getRank().getTier() <= ConfigHandler.deathMessageTier) {
                entityLivingBase.getServer().getPlayerList().sendMessage(new TextComponentTranslation("champions.identifier")
                        .appendSibling(new TextComponentString(" "))
                        .appendSibling(entityLivingBase.getCombatTracker().getDeathMessage()));
            }
        }
    }

    @SubscribeEvent
    public void livingDrops(LivingDropsEvent evt) {
        EntityLivingBase entity = evt.getEntityLiving();

        if (ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);

            if (chp != null && ChampionHelper.isElite(chp.getRank())) {

                if (entity.world instanceof WorldServer) {

                    if (ConfigHandler.lootSource != ConfigHandler.LootSource.CONFIG) {
                        WorldServer world = (WorldServer) entity.world;
                        LootTable table = world.getLootTableManager().getLootTableFromLocation(CHAMPION_LOOT);
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

                    if (ConfigHandler.lootSource != ConfigHandler.LootSource.LOOT_TABLE) {
                        ItemStack loot = ChampionHelper.getLootDrop(chp.getRank().getTier());

                        if (!loot.isEmpty()) {
                            EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, loot);
                            entityitem.setDefaultPickupDelay();
                            evt.getDrops().add(entityitem);
                        }
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

    //Increase attack damage for attackers even if they don't have the attack damage attribute
    @SubscribeEvent
    public void livingDamage(LivingHurtEvent evt) {

        if (evt.getSource().getTrueSource() instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving)evt.getSource().getTrueSource();

            if (entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null
                    && ChampionHelper.isValidChampion(entity)) {
                IChampionship chp = CapabilityChampionship.getChampionship(entity);

                if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                    evt.setAmount(evt.getAmount() * (float)(1 + ConfigHandler.growth.attackDamage * chp.getRank()
                            .getTier()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Start evt) {
        Explosion explosion = evt.getExplosion();
        EntityLivingBase entityLivingBase = explosion.getExplosivePlacedBy();

        if (entityLivingBase instanceof EntityCreeper && !entityLivingBase.world.isRemote) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityCreeper)entityLivingBase);

            if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                explosion.size += ConfigHandler.growth.creeperStrength * chp.getRank().getTier();
            }
        }
    }

    @SubscribeEvent
    public void injuredPotionHealHandler(LivingHealEvent evt) {
        EntityLivingBase entity = evt.getEntityLiving();

        if (entity.isPotionActive(ChampionsRegistry.injured)) {
            evt.setAmount(evt.getAmount() * 0.6f);
        }
    }

    @SubscribeEvent
    public void injuredPotionHurtHandler(LivingDamageEvent evt) {

        if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.injured)) {
            evt.setAmount(evt.getAmount() * 1.4f);
        }
    }
}
