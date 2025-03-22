package net.countercraft.movecraft.combat.features.directors.listener;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.*;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.processing.functions.Result;
import net.countercraft.movecraft.sign.AbstractMovecraftSign;
import net.countercraft.movecraft.sign.AbstractSubcraftSign;
import net.countercraft.movecraft.sign.MovecraftSignRegistry;
import net.countercraft.movecraft.sign.SignListener;
import net.countercraft.movecraft.util.Pair;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CraftPilotListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDetect(final CraftPilotEvent event) {
        if (event.getReason() == CraftPilotEvent.Reason.SUB_CRAFT) {
            return;
        }
        if (event.getCraft() instanceof SinkingCraft || event.getCraft() instanceof SubCraft) {
            return;
        }

        flagTileEntities(event.getCraft(), block -> (block instanceof Dispenser));
        flagSubcraftDispensers(event.getCraft());
    }

    private static void flagSubcraftDispensers(Craft craft) {
        Map<MovecraftLocation, Map<CraftType, String>> signLocations = new HashMap<>();
        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Block block = mLoc.toBukkit(craft.getWorld()).getBlock();

            // Only interested in signs, if no sign => continue
            if (!(block.getState() instanceof Sign))
                continue;

            Sign sign = (Sign) block.getState();

            for (SignListener.SignWrapper signWrapper : SignListener.INSTANCE.getSignWrappers(sign, true)) {
                if (signWrapper.getRaw(3) == null || signWrapper.getRaw(3).isBlank()) {
                    continue;
                }

                AbstractMovecraftSign ams = MovecraftSignRegistry.INSTANCE.get(signWrapper.line(0));
                // Now, are you a subcraft sign?
                if (ams instanceof AbstractSubcraftSign ass) {
                    CraftType craftType = CraftManager.getInstance().getCraftTypeFromString(signWrapper.getRaw(1));
                    if (craftType == null) {
                        continue;
                    }
                    signLocations.computeIfAbsent(mLoc, b -> new HashMap<>()).put(craftType, signWrapper.getRaw(3));
                }
            }
        }

        if (signLocations.isEmpty()) {
            return;
        }

        for (Map.Entry<MovecraftLocation, Map<CraftType, String>> entry : signLocations.entrySet()) {
            for (Map.Entry<CraftType, String> entryInner : entry.getValue().entrySet()) {
                CraftManager.getInstance().detect(
                        entry.getKey(),
                        entryInner.getKey(),
                        (type, world, player, parents) -> {
                            if (parents.size() > 1) 
                                return new Pair<>(Result.fail(), null);
                            
                            return new Pair<>(Result.succeed(), new SubCraftImpl(type, world, craft));
                        },
                        craft.getWorld(),
                        null,
                        Audience.empty(),
                        (Craft subcraft) -> () -> flagDispensers(subcraft, craft, entryInner.getValue())
                );
            }
        }
    }

    private static void flagDispensers(Craft subcraft, Craft parent, String value) {
        for (MovecraftLocation mLoc : subcraft.getHitBox()) {
            Block block = mLoc.toBukkit(parent.getWorld()).getBlock();
            if (!(block.getState() instanceof Container))
                continue;

            Container blockEntity = (Container)block.getState();

            if (blockEntity.customName() == null) {
                blockEntity.customName(Component.text(value));
                blockEntity.update();
            }

        }
        parent.setHitBox(parent.getHitBox().union(subcraft.getHitBox()));
        CraftManager.getInstance().release(subcraft, CraftReleaseEvent.Reason.FORCE, true);

    }

    private static void flagTileEntities(Craft craft, Predicate<BlockState> testPredicate) {
        // Now, find all signs on the craft...
        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Block block = mLoc.toBukkit(craft.getWorld()).getBlock();
            // Only interested in signs, if no sign => continue
            // Edit: That's useful for dispensers too to flag TNT and the like, but for that one could use a separate listener
            if (!testPredicate.test(block.getState()))
                continue;
            if (!(block.getState() instanceof TileState))
                continue;

            TileState tile = (TileState) block.getState();

            craft.markTileStateWithUUID(tile);
            tile.update();
        }
    }
}
