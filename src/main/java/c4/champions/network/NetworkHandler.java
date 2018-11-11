package c4.champions.network;

import c4.champions.Champions;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(Champions.MODID);

    public static void register() {
        INSTANCE.registerMessage(PacketSyncAffix.PacketSyncHandler.class, PacketSyncAffix.class, 0, Side.CLIENT);
    }

}
