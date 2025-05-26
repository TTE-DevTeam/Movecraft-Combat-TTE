package net.countercraft.movecraft.combat.features.directors.listener;

import de.dertoaster.extraevents.api.event.ExplosionPropellTNTEvent;
import net.countercraft.movecraft.combat.features.directors.CraftDirectorData;
import net.countercraft.movecraft.combat.features.directors.DirectorHelper;
import net.countercraft.movecraft.combat.features.directors.TNTDirectorDataAccess;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class TNTPropellListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTNTPropell(final ExplosionPropellTNTEvent event) {
        // 0) Check if the tnt is fast enough
        if (event.getProjectile().getVelocity().lengthSquared() <= 0.35) {
            return;
        }
        // 1) Do we need to direct?
        if (!DirectorHelper.isTNTFlaggedForDirection(event.getProjectile())) {
            return;
        }
        // 2) Did we already direct this entity?
        if (!TNTDirectorDataAccess.wasAlreadyDirected(event.getProjectile())) {
            // 2.1) No? => Send call to the relevant craft and it's directors to handle it
            Craft craft = MathUtils.fastNearestCraftToLoc(CraftManager.getInstance().getCrafts(), event.getProjectile().getLocation());
            if (craft == null) {
                return;
            }
            CraftDirectorData.get(craft).attemptDirectEntity(event.getEntity());
            event.setCancelled(true);
        } else {
            // 2.2) Yes? => If the time of initial direction is not too long ago (aka in the same tick!), Add onto the original modified velocity (needs to be stored!) and apply the length onto the directed velocity!
            // 3) In case it was directed or we increased our direction-velocity, we need to cancel the event!
            if (TNTDirectorDataAccess.wasDirectedInSameTick(event.getProjectile())) {
                Vector potentialPush = event.getPushDirection();
                Vector originalVelocity = TNTDirectorDataAccess.getPreDirectVelocity(event.getProjectile()).clone();
                originalVelocity.add(potentialPush);
                TNTDirectorDataAccess.setPreDirectionVelocity(event.getProjectile(), originalVelocity);
                double power = originalVelocity.length();
                Vector currentVelocity = event.getEntity().getVelocity();
                event.getProjectile().setVelocity(currentVelocity.normalize().multiply(power));

                event.setCancelled(true);
            } else {
                // Nothing to do...
            }
        }
    }

}
