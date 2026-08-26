package dev.xyat.kineticdummy.client;

import dev.xyat.kineticdummy.dummy.config.DummyClientConfig;
import dev.xyat.kineticdummy.dummy.config.DummyConfigGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public final class KineticDummyClientBootstrap {
    private KineticDummyClientBootstrap() {
    }

    public static void register(FMLJavaModLoadingContext context) {
        DummyClientConfig.register(context);
        DummyConfigGui.load();
    }
}
