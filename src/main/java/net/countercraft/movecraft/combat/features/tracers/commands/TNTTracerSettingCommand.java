package net.countercraft.movecraft.combat.features.tracers.commands;

import io.papermc.paper.command.brigadier.Commands;
import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.features.tracers.config.PlayerConfig;
import net.countercraft.movecraft.combat.utils.EnumArgumentType;
import org.bukkit.entity.Player;

import java.util.List;

public class TNTTracerSettingCommand {

    public static void register(final Commands commands) {
        commands.register(
                Commands.literal("tnttracersetting")
                        .requires(source -> {
                            return (source.getExecutor() instanceof Player);
                        })
                        .then(Commands.argument("tracersetting", EnumArgumentType.ofEnum(PlayerConfig.TNTSetting.class))
                                .executes(context -> {
                                    MovecraftCombat.getInstance().getPlayerManager().setTNTSetting((Player) context.getSource().getExecutor(), context.getArgument("tracersetting", PlayerConfig.TNTSetting.class));
                                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                                })
                        )
                        .build(),
                "Command to change cannon tracer settings",
                List.of()
        );
    }

}
