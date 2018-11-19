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
