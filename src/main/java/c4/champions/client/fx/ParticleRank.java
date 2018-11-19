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

package c4.champions.client.fx;

import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleRank extends ParticleSpell {

    public ParticleRank(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeed, double ySpeed,
                        double zSpeed, int color) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeed, ySpeed, zSpeed);
        this.setBaseSpellTextureIndex(144);
        float f = worldIn.rand.nextFloat() * 0.5F + 0.35F;
        float r = (float)((color>>16)&0xFF)/255f;
        float g = (float)((color>>8)&0xFF)/255f;
        float b = (float)((color)&0xFF)/255f;
        this.setRBGColorF(r * f, g * f, b * f);
    }
}
