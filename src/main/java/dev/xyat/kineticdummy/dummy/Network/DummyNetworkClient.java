package dev.xyat.kineticdummy.dummy.Network;

import dev.xyat.kineticdummy.dummy.client.DeathSummaryOverlay;
import dev.xyat.kineticdummy.dummy.client.DummyTextManager;
import dev.xyat.kineticdummy.dummy.client.NotifyManager;
import dev.xyat.kineticdummy.dummy.config.DummyClientConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DummyNetworkClient {
    public static void handleSync(DummyNetwork.Sync packet) {
        if (packet.type == DummyNetwork.Sync.Type.REALTIME) {
            DummyTextManager.handlePacket(
                    packet.entityId,
                    packet.name,
                    packet.typeName,
                    packet.total,
                    packet.dps,
                    packet.avgDps,
                    packet.hits,
                    packet.currentDamage,
                    packet.isDummy,
                    packet.minionOwnerId
            );
        } else if (DummyClientConfig.showDeathSummary.get()) {
            DeathSummaryOverlay.show(packet.name, packet.total, packet.dps, packet.time, packet.hits);
        }
    }

    public static void handleNotify(DummyNetwork.SyncNotify packet) {
        NotifyManager.notify(packet.msg());
    }
}
