/*
 * Copyright (C) 2018  C4
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

package c4.champions.common.potion;

import c4.champions.Champions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class PotionPlague extends Potion {

    private static final ResourceLocation ICON = new ResourceLocation(Champions.MODID, "textures/gui/plague.png");

    public PotionPlague() {
        super(true, 2046740);
        this.setPotionName(Champions.MODID + ".plague");
        this.setRegistryName(Champions.MODID, "plague");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {

        if (!entityLivingBaseIn.world.isRemote) {
            List<EntityLivingBase> entities = entityLivingBaseIn.world.getEntitiesWithinAABB(EntityLivingBase.class,
                    entityLivingBaseIn.getEntityBoundingBox().grow(4, 4, 4));

            if (!entities.isEmpty()) {

                for (EntityLivingBase ent : entities) {

                    if (!ent.isPotionActive(MobEffects.WITHER) || (ent.isPotionActive(MobEffects.WITHER) && ent
                            .getActivePotionEffect(MobEffects.WITHER).getDuration() <= 20)) {
                        ent.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0));
                    }

                    if (!ent.isPotionActive(this) && ent != entityLivingBaseIn) {
                        ent.addPotionEffect(new PotionEffect(this, 300));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        if (mc.currentScreen != null) {
            mc.getTextureManager().bindTexture(ICON);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }
}
