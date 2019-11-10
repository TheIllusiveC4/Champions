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

package c4.champions.client.renderer;

import c4.champions.Champions;
import c4.champions.common.entity.EntityArcticSpark;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderArcticSpark extends AbstractRenderSpark<EntityArcticSpark> {

    public static final Factory FACTORY = new Factory();

    private static final ResourceLocation SPARK_TEXTURE = new ResourceLocation(Champions.MODID,
            "textures/entity/arcticspark.png");

    public RenderArcticSpark(RenderManager manager)
    {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityArcticSpark entity) {
        return SPARK_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityArcticSpark> {

        @Override
        public Render<? super EntityArcticSpark> createRenderFor(RenderManager manager) {
            return new RenderArcticSpark(manager);
        }
    }
}
