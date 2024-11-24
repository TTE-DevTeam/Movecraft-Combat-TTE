package net.countercraft.movecraft.combat.listener;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.PlayerCraftImpl;
import net.countercraft.movecraft.craft.SubCraftImpl;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.processing.functions.Result;
import net.countercraft.movecraft.util.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CraftAssembleListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAssembly(@NotNull CraftPilotEvent event) {
        // We're not interested in subcraft assembly
        if (event.getReason() == CraftPilotEvent.Reason.SUB_CRAFT)
            return;

        final Craft craft = event.getCraft();

        // Now, find all signs on the craft...
        for (MovecraftLocation mLoc : craft.getHitBox()) {
            // I hate this...
            // TODO: Switch to use CCCorp version with blockName(...) => Only returns tracked or all signs directly
            Block block = mLoc.toBukkit(craft.getWorld()).getBlock();
            // Only interested in signs, if no sign => continue
            if (!(block.getState() instanceof Sign))
                continue;
            // Now, are you a subcraft sign?
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            // We only care about signs with actual data...
            if (lines.length == 0 || lines[0].isEmpty())
                continue;

            // Are you a subcraft sign?
            if (lines[0].toLowerCase().startsWith("subcraft")) {
                // We are subcraft!
                // Now, do you have an actual name?
                if (lines[2].isEmpty())
                    continue;

                final String subcraftIdent = lines[2];
                // Ok, you have a name, cool
                // Now, let's assemble you and mark your dispensers...
                String craftTypeIdent = lines[1];
                if (craftTypeIdent.isEmpty())
                    continue;

                CraftType craftType = CraftManager.getInstance().getCraftTypeFromString(craftTypeIdent);
                if (craftType == null)
                    continue;

                // Assemble!
                CraftManager.getInstance().detect(
                        mLoc,
                        craftType,
                        (type, w, p, parents) -> {
                            if (parents.size() > 1)
                                return new Pair<>(Result.fail(), null);

                            return new Pair<>(Result.succeed(), new SubCraftImpl(type, w, craft));
                        },
                        craft.getWorld(),
                        null,
                        Movecraft.getAdventure().console(),
                        (Craft subcraft) -> () -> flagDispensers(subcraft, craft, subcraftIdent)
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAssemblyFlagTiles(@NotNull CraftPilotEvent event) {
        // We're not interested in subcraft assembly
        if (event.getReason() == CraftPilotEvent.Reason.SUB_CRAFT)
            return;

        final Craft craft = event.getCraft();
        if (!(craft instanceof PlayerCraftImpl)) {
            return;
        }
        PlayerCraftImpl playerCraft = (PlayerCraftImpl) craft;
        // TODO: Introduce proper craft-UUIDs in movecraft
        final UUID craftID = null;
        if (craftID == null) {
            return;
        }

        // Now, find all signs on the craft...
        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Block block = mLoc.toBukkit(craft.getWorld()).getBlock();
            // Now, flag it with the crafts id
            if (!(block.getState() instanceof TileState)) {
                continue;
            }

            TileState blockEntity = (TileState)block.getState();
            blockEntity.getPersistentDataContainer().set(

            );
        }
    }

    // TODO: Should we rather use the containername maybe?
    protected static void flagDispensers(Craft subcraft, Craft parent, final String subcraftIdent) {
        for (MovecraftLocation mLoc : subcraft.getHitBox()) {
            Block block = mLoc.toBukkit(parent.getWorld()).getBlock();
            if (!(block.getState() instanceof TileState))
                continue;

            TileState blockEntity = (TileState)block.getState();
            blockEntity.getPersistentDataContainer().set(
                    MovecraftCombat.getInstance().KEY_DIRECTOR_NODE_IDENT,
                    PersistentDataType.STRING,
                    subcraftIdent
            );
        }
    }

}
