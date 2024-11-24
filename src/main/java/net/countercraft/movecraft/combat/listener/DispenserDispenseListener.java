package net.countercraft.movecraft.combat.listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.countercraft.movecraft.combat.utils.BlockHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.projectiles.BlockProjectileSource;

import java.util.Optional;

public class DispenserDispenseListener implements Listener {

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        // TODO: Flag fireballs, arrows, tnt etc with the dispensers flags...
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileSpawnEvent(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity.getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.DISPENSE_EGG) {
            return;
        }

        if (!(entity instanceof Projectile)) {
            return;
        }
        Projectile proj = (Projectile)entity;
        if (!(proj.getShooter() instanceof BlockProjectileSource)) {
            return;
        }

        BlockProjectileSource blockProjSource = (BlockProjectileSource) proj.getShooter();

        Block block = blockProjSource.getBlock();
        if (block == null) {
            return;
        }

        Optional<String> optData = BlockHelper.tryGetDirectorNodeIdent(block);
        if (optData.isEmpty()) {
            return;
        }
        flagEntity(entity, optData);
    }

    @EventHandler(ignoreCancelled = false)
    public void onExplosionPriming(EntityKnockbackByEntityEvent event) {
        event.getCause()
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileSpawnEventPostFlag(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Projectile)) {
            return;
        }
        Projectile proj = (Projectile)entity;
        // NOw,
    }


    protected static void flagEntity(Entity entity, Optional<String> optFlag) {
        if (optFlag.isEmpty()) {
            return;
        }
        // TODO: Flag entity
    }

}
