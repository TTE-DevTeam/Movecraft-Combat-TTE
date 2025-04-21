package net.countercraft.movecraft.combat.features.directors.listener;

import de.dertoaster.extraevents.api.event.ExplosionPropellTNTEvent;
import net.countercraft.movecraft.combat.features.directors.DirectorHelper;
import net.countercraft.movecraft.combat.features.directors.TNTDirectorDataAccess;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class TNTPropellListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTNTPropell(final ExplosionPropellTNTEvent event) {
        // 1) Do we need to direct?
        if (!DirectorHelper.isTNTFlaggedForDirection(event.getProjectile())) {
            return;
        }
        // 2) Did we already direct this entity?
        if (!TNTDirectorDataAccess.wasAlreadyDirected(event.getProjectile())) {
            //   2.1) No? => Send call to the relevant craft and it's directors to handle it
            // TODO: Update dependency
            //event.setCancelled(true);
        } else {
            //   2.2) Yes? => If the time of initial direction is not too long ago (aka in the same tick!), Add onto the original modified velocity (needs to be stored!) and apply the length onto the directed velocity!
            // 3) In case it was directed or we increased our direction-velocity, we need to cancel the event!
            if (TNTDirectorDataAccess.wasDirectedInSameTick(event.getProjectile())) {
                Vector potentialPush = null;//event.getPushDirection();
                double power = potentialPush.length();
                Vector currentVelocity = event.getEntity().getVelocity();
                double resultingPower = currentVelocity.length() + power;
                event.getProjectile().setVelocity(currentVelocity.normalize().multiply(resultingPower));

                //event.setCancelled(true);
            } else {
                // Nothing to do...
            }
        }
    }

}
