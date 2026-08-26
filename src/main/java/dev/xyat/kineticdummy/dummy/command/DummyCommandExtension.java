package dev.xyat.kineticdummy.dummy.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xyat.kineticcore.command.CommandUtils;
import dev.xyat.kineticcore.command.KTCommandApi;
import dev.xyat.kineticcore.command.KTCommandExtension;
import dev.xyat.kineticdummy.KineticDummy;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public final class DummyCommandExtension implements KTCommandExtension {
    private DummyCommandExtension() {
    }

    public static void install() {
        KTCommandApi.register(KineticDummy.MODID, new DummyCommandExtension());
    }

    @Override
    public void registerCommands(LiteralArgumentBuilder<CommandSourceStack> root) {
        DummyCommand.register(root);
    }

    @Override
    public void appendHelpItems(CommandSourceStack source, List<MutableComponent> items) {
        if (!source.hasPermission(2)) {
            return;
        }
        items.add(CommandUtils.createExecutableCommand(
                "/kt dummy",
                "cmd.kineticdummy.dummy.desc"
        ));
    }

    @Override
    public void reload(CommandSourceStack source) {
    }
}
