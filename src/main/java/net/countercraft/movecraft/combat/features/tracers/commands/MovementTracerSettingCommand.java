package net.countercraft.movecraft.combat.features.tracers.commands;

import io.papermc.paper.command.brigadier.Commands;
import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.features.tracers.config.PlayerConfig;
import net.countercraft.movecraft.combat.utils.EnumArgumentType;
import org.bukkit.entity.Player;

import java.util.List;

public class MovementTracerSettingCommand {

    public static void register(final Commands commands) {
        commands.register(
                Commands.literal("movementtracersetting")
                        .requires(source -> {
                            return (source.getExecutor() instanceof Player);
                        })
                        .then(Commands.argument("densitysetting", EnumArgumentType.ofEnum(PlayerConfig.MovementSetting.class))
                                .executes(context -> {
                                    MovecraftCombat.getInstance().getPlayerManager().setMovementSetting((Player) context.getSource().getExecutor(), context.getArgument("densitysetting", PlayerConfig.MovementSetting.class));
                                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                                })
                        )
                        .build(),
                "Command to change movement tracer settings",
                List.of()
        );
    }

}
