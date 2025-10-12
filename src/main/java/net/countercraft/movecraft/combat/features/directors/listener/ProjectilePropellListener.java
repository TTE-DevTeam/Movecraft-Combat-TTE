package net.countercraft.movecraft.combat.features.directors.listener;

import de.dertoaster.extraevents.api.event.ExplosionPropellProjectileEvent;
import net.countercraft.movecraft.combat.features.directors.CraftDirectorData;
import net.countercraft.movecraft.combat.features.directors.DirectorDataAccess;
import net.countercraft.movecraft.combat.features.directors.DirectorHelper;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

public class ProjectilePropellListener implements Listener {

    public void onProjectilePropell(final ExplosionPropellProjectileEvent event) {
        // 0) Check if the projectile is fast enough
        if (event.getProjectile().getVelocity().lengthSquared() <= 0.35) {
            return;
        }
        Block block = null;
        if (event.getProjectile().getShooter() instanceof BlockProjectileSource bps) {
            block = bps.getBlock();
        }
        if (block == null) {
            return;
        }
        // 1) Get the craft!
        Craft craft = MathUtils.getCraftByPersistentBlockData(block.getLocation());
        if (DirectorHelper.canBeDirectedAgain(event.getProjectile(), craft)) {
            DirectorDataAccess.setPreDirectionVelocity(event.getEntity(), event.getEntity().getVelocity().clone());
            if (CraftDirectorData.get(craft).attemptDirectEntity(event.getEntity())) {
                DirectorHelper.flagProjectileAsDirected((Projectile) event.getEntity(), craft, true);
                event.setCancelled(true);
                return;
            }
        }
        // 2) If we didnt do anything, accelerate the projectile!
        if (DirectorDataAccess.wasAlreadyDirected(event.getProjectile())) {
            Vector potentialPush = event.getPushDirection();
            Vector originalVelocity = DirectorDataAccess.getPreDirectVelocity(event.getProjectile()).clone();
            originalVelocity.add(potentialPush);
            DirectorDataAccess.setPreDirectionVelocity(event.getProjectile(), originalVelocity);
            double power = originalVelocity.length();
            Vector currentVelocity = event.getEntity().getVelocity();
            event.getProjectile().setVelocity(currentVelocity.normalize().multiply(power));

            event.setCancelled(true);
        }
    }

}
