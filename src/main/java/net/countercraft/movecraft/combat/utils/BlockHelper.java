package net.countercraft.movecraft.combat.utils;

import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class BlockHelper {

    // TODO: Should we rather use the containername maybe?
    public static Optional<String> tryGetDirectorNodeIdent(final Block block) {
        if (!(block.getState() instanceof TileState))
            return Optional.empty();

        TileState blockEntity = (TileState)block.getState();

        if (!blockEntity.getPersistentDataContainer().has(MovecraftCombat.getInstance().KEY_DIRECTOR_NODE_IDENT, PersistentDataType.STRING)) {
            return Optional.empty();
        }

        return Optional.ofNullable(blockEntity.getPersistentDataContainer().getOrDefault( MovecraftCombat.getInstance().KEY_DIRECTOR_NODE_IDENT, PersistentDataType.STRING, null));
    }

}
