package net.countercraft.movecraft.combat.features.tracers.commands;

import io.papermc.paper.command.brigadier.Commands;
import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.features.tracers.config.PlayerConfig;
import net.countercraft.movecraft.combat.utils.EnumArgumentType;
import org.bukkit.entity.Player;

import java.util.List;

public class TNTTracerModeCommand {

    public static void register(final Commands commands) {
        commands.register(
                Commands.literal("tnttracermode")
                        .requires(source -> {
                            return (source.getExecutor() instanceof Player);
                        })
                        .then(Commands.argument("tracermode", EnumArgumentType.ofEnum(PlayerConfig.TNTMode.class))
                                .executes(context -> {
                                    MovecraftCombat.getInstance().getPlayerManager().setTNTMode((Player) context.getSource().getExecutor(), context.getArgument("tracermode", PlayerConfig.TNTMode.class));
                                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                                })
                        )
                        .build(),
                "Command to change cannon tracer settings",
                List.of()
        );
    }

}
