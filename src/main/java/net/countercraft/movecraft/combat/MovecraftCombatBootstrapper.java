package net.countercraft.movecraft.combat;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.countercraft.movecraft.combat.features.tracers.commands.MovementTracerSettingCommand;
import net.countercraft.movecraft.combat.features.tracers.commands.TNTTracerModeCommand;
import net.countercraft.movecraft.combat.features.tracers.commands.TNTTracerSettingCommand;

public class MovecraftCombatBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        // Commands
        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, this::registerCommands);
    }

    private void registerCommands(ReloadableRegistrarEvent<Commands> event) {
        final Commands commands = event.registrar();

        TNTTracerModeCommand.register(commands);
        TNTTracerSettingCommand.register(commands);
        MovementTracerSettingCommand.register(commands);
    }

}
