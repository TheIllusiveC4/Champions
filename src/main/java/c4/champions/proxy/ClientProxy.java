package c4.champions.proxy;

import c4.champions.client.EventHandlerClient;
import c4.champions.client.layer.LayerRank;
import c4.champions.client.layer.LayerShielding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.util.Map;
import java.util.Random;

public class ClientProxy implements IProxy {

    private static final Random rand = new Random();

    @Override
    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {
        Map<Class <? extends Entity> , Render<? extends Entity >> renderMap = Minecraft.getMinecraft().getRenderManager()
                .entityRenderMap;

        for (Render<? extends Entity> render : renderMap.values()) {

            if (render instanceof RenderLiving) {
                RenderLiving livingRender = (RenderLiving)render;
                livingRender.addLayer(new LayerShielding(livingRender, livingRender.getMainModel()));
                livingRender.addLayer(new LayerRank(livingRender, livingRender.getMainModel()));
            }
        }
    }
}
