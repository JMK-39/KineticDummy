package dev.xyat.kineticdummy.dummy.config;

import dev.xyat.kineticcore.config.client.KTClientConfigAdapter;
import dev.xyat.kineticcore.config.client.KTConfigApi;
import dev.xyat.kineticcore.config.client.KTConfigPage;
import dev.xyat.kineticcore.config.client.KTConfigScope;
import dev.xyat.kineticdummy.dummy.client.DeathSummaryOverlay;
import dev.xyat.kineticdummy.dummy.client.DummyTextManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;

public final class DummyConfigGui {
    /** Kept for add-ons that already open the original client display page directly. */
    public static final String PAGE_ID = "kineticdummy:dummy";
    public static final String SERVER_PAGE_ID = "kineticdummy:server";

    private DummyConfigGui() {
    }

    public static void load() {
        KTConfigApi.register(buildClientPage());
        KTConfigApi.register(buildServerPage());
    }

    private static KTConfigPage buildClientPage() {
        return KTClientConfigAdapter.pageBuilder(
                        PAGE_ID,
                        Component.translatable("cfg.kineticdummy.dummy.category"),
                        DummyClientConfig.SPEC,
                        DummyConfigGui::includeAutomaticClientField
                )
                .pageDescription(Component.translatable("cfg.kineticdummy.dummy.client.description"))
                .applyTiming(KTConfigPage.ApplyTiming.IMMEDIATE)
                .section(Component.translatable("cfg.kineticdummy.dummy.visual_details"))
                .tickSecondsValue("summary_duration", Component.translatable("cfg.kineticdummy.dummy.summaryDuration"),
                        DummyClientConfig.summaryDuration, DummyClientConfig.summaryDuration::set,
                        100, 20, 600, Component.translatable("cfg.kineticdummy.dummy.summaryDuration.tooltip"))
                .color("color_normal", Component.translatable("cfg.kineticdummy.dummy.colorNormal"),
                        DummyClientConfig.colorNormal, DummyClientConfig.colorNormal::set, 0xFF69B4,
                        Component.translatable("cfg.kineticdummy.dummy.colorNormal.tooltip"))
                .color("color_crit", Component.translatable("cfg.kineticdummy.dummy.colorCrit"),
                        DummyClientConfig.colorCrit, DummyClientConfig.colorCrit::set, 0xFF5555,
                        Component.translatable("cfg.kineticdummy.dummy.colorCrit.tooltip"))
                .color("color_minion", Component.translatable("cfg.kineticdummy.dummy.colorMinion"),
                        DummyClientConfig.colorMinion, DummyClientConfig.colorMinion::set, 0x55FF55,
                        Component.translatable("cfg.kineticdummy.dummy.colorMinion.tooltip"))
                .color("color_overhead_source", Component.translatable("cfg.kineticdummy.dummy.colorOverSource"),
                        DummyClientConfig.colorOverheadSource, DummyClientConfig.colorOverheadSource::set, 0xBBFFFF,
                        Component.translatable("cfg.kineticdummy.dummy.colorOverSource.tooltip"))
                .color("color_overhead_type", Component.translatable("cfg.kineticdummy.dummy.colorOverType"),
                        DummyClientConfig.colorOverheadType, DummyClientConfig.colorOverheadType::set, 0xFFFF55,
                        Component.translatable("cfg.kineticdummy.dummy.colorOverType.tooltip"))
                .color("color_overhead_stats", Component.translatable("cfg.kineticdummy.dummy.colorOverStats"),
                        DummyClientConfig.colorOverheadStats, DummyClientConfig.colorOverheadStats::set, 0x55FF55,
                        Component.translatable("cfg.kineticdummy.dummy.colorOverStats.tooltip"))
                .color("color_overhead_dps", Component.translatable("cfg.kineticdummy.dummy.colorOverDps"),
                        DummyClientConfig.colorOverheadDps, DummyClientConfig.colorOverheadDps::set, 0x00F6F6,
                        Component.translatable("cfg.kineticdummy.dummy.colorOverDps.tooltip"))
                .color("color_summary_title", Component.translatable("cfg.kineticdummy.dummy.colorSumTitle"),
                        DummyClientConfig.colorSummaryTitle, DummyClientConfig.colorSummaryTitle::set, 0xFFAA00,
                        Component.translatable("cfg.kineticdummy.dummy.colorSumTitle.tooltip"))
                .color("color_summary_stats", Component.translatable("cfg.kineticdummy.dummy.colorSumStats"),
                        DummyClientConfig.colorSummaryStats, DummyClientConfig.colorSummaryStats::set, 0x55FF55,
                        Component.translatable("cfg.kineticdummy.dummy.colorSumStats.tooltip"))
                .color("color_summary_time", Component.translatable("cfg.kineticdummy.dummy.colorSumTime"),
                        DummyClientConfig.colorSummaryTime, DummyClientConfig.colorSummaryTime::set, 0x55FFFF,
                        Component.translatable("cfg.kineticdummy.dummy.colorSumTime.tooltip"))
                .onSave(DummyConfigGui::saveClientConfig)
                .build();
    }

    private static boolean includeAutomaticClientField(String path) {
        int separator = path.lastIndexOf('.');
        String leaf = separator < 0 ? path : path.substring(separator + 1);
        return !leaf.startsWith("color") && !"DeathSummaryHUD.durationTicks".equals(path);
    }

    private static void saveClientConfig() {
        DummyClientConfig.SPEC.save();
        if (!DummyClientConfig.showDamageParticles.get()) DummyTextManager.clearDamageParticles();
        if (!DummyClientConfig.showMinionDamage.get()) DummyTextManager.clearMinionDamageParticles();
        if (!DummyClientConfig.showDeathSummary.get()) DeathSummaryOverlay.clear();
        DummyTextManager.clearAllDamageNumbers();
    }

    private static KTConfigPage buildServerPage() {
        return KTConfigPage.builder(SERVER_PAGE_ID, Component.translatable("cfg.kineticdummy.dummy.server.category"))
                .scope(KTConfigScope.SERVER_AUTHORITATIVE)
                .serverManaged()
                .applyTiming(KTConfigPage.ApplyTiming.MIXED)
                .applyNotice(Component.translatable("cfg.kineticdummy.dummy.apply_notice"))
                .pageDescription(Component.translatable("cfg.kineticdummy.dummy.server.description"))
                .itemRuleList("equipment_blacklist", Component.translatable("cfg.kineticdummy.dummy.blacklist"),
                        () -> new ArrayList<>(DummyConfig.equipmentBlacklist.get()),
                        values -> DummyConfig.equipmentBlacklist.set(new ArrayList<>(values)),
                        Arrays.asList("kineticcore:levitation_backpack", "somerandomitem:infinite_potion"),
                        Component.translatable("cfg.kineticdummy.dummy.blacklist.tooltip"))
                .intValue("standby_range", Component.translatable("cfg.kineticdummy.dummy.standbyRange"),
                        DummyConfig.dummyStandbyRange, DummyConfig.dummyStandbyRange::set,
                        16, 0, 64, Component.translatable("cfg.kineticdummy.dummy.standbyRange.tooltip"))
                .intValue("broadcast_range", Component.translatable("cfg.kineticdummy.dummy.broadcastRange"),
                        DummyConfig.dummyBroadcastRange, DummyConfig.dummyBroadcastRange::set,
                        32, 0, 256, Component.translatable("cfg.kineticdummy.dummy.broadcastRange.tooltip"))
                .tickSecondsValue("standby_check_interval", Component.translatable("cfg.kineticdummy.dummy.standbyCheckInterval"),
                        DummyConfig.dummyStandbyCheckIntervalTicks, DummyConfig.dummyStandbyCheckIntervalTicks::set,
                        20, 1, 200, Component.translatable("cfg.kineticdummy.dummy.standbyCheckInterval.tooltip"))
                .tickSecondsValue("sync_interval", Component.translatable("cfg.kineticdummy.dummy.syncInterval"),
                        DummyConfig.dummySyncIntervalTicks, DummyConfig.dummySyncIntervalTicks::set,
                        2, 1, 20, Component.translatable("cfg.kineticdummy.dummy.syncInterval.tooltip"))
                .intValue("curio_extra_slots", Component.translatable("cfg.kineticdummy.dummy.curioExtraSlots"),
                        DummyConfig.dummyCurioExtraSlots, DummyConfig.dummyCurioExtraSlots::set,
                        53, 0, 53, Component.translatable("cfg.kineticdummy.dummy.curioExtraSlots.tooltip"))
                .build();
    }

    public static Screen create(Screen parent) {
        return KTConfigApi.createScreen(parent, PAGE_ID);
    }
}
