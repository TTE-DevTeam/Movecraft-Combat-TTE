package net.countercraft.movecraft.combat.features.directors.listener;

import de.dertoaster.extraevents.api.event.ExplosionPropellTNTEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TNTPropellListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTNTPropell(final ExplosionPropellTNTEvent event) {
        // TODO: React to this!
    }

}
