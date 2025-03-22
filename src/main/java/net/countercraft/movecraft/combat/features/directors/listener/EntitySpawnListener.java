package net.countercraft.movecraft.combat.features.directors.listener;

import net.countercraft.movecraft.combat.event.DispenserDispenseEntityEvent;
import net.countercraft.movecraft.combat.features.directors.DirectorHelper;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.projectiles.BlockProjectileSource;

import java.util.function.Predicate;

public class EntitySpawnListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDispenserSpawnEntity(DispenserDispenseEntityEvent event) {
        flagEntity(e -> {
            if (event.getEntity() == null || event.getEntity() instanceof Projectile) {
                return false;
            }
            if (!(event.getEntity() instanceof TNTPrimed)) {
                return false;
            }
            return true;
        }, event.getBlock(), event.getEntity());

    }

    protected static void flagEntity(Predicate<Entity> testPredicate, Block block, Entity entity) {
        if (block == null) {
            return;
        }

        if (!(block.getState() instanceof Container)) {
            return;
        }
        Container container = (Container) block.getState();

        if (!(testPredicate.test(entity))) {
            return;
        }

        String containerName = PlainTextComponentSerializer.plainText().serialize(container.customName());

        DirectorHelper.flagEntity(entity, containerName);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileSpawnEvent(EntitySpawnEvent event) {
        Block block = event.getLocation().getBlock();
        if (event.getEntity() instanceof Projectile projectile && projectile.getShooter() instanceof BlockProjectileSource bps) {
            block = bps.getBlock();
        }
        // Flag entity for named directors
        flagEntity(e -> {
            if (!(e instanceof Projectile)) {
                return false;
            }

            Projectile projectile = (Projectile) e;

            if (!(projectile.getShooter() instanceof BlockProjectileSource)) {
                return false;
            }
            return true;
        }, block, event.getEntity());

        // Director events
        Craft craft = MathUtils.getCraftByPersistentBlockData(block.getLocation());
        if (craft == null) {
            return;
        }
        //Call the director event for the craft, rest is handled on the craft itself
    }

}
