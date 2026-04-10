package net.countercraft.movecraft.combat.features.directors.listener;

import net.countercraft.movecraft.combat.features.directors.Directors;
import net.countercraft.movecraft.combat.features.directors.LivingEntityDirector;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EntityInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!(event.getAction().isLeftClick() || event.getAction().isRightClick())) {
            return;
        }
        final Material item = event.getItem().getType();

        if (item != Directors.DirectorTool) {
            return;
        }

        // Find the directors for this player and save the vector or reset it, depending on what we need
        LivingEntityDirector director = LivingEntityDirector.of(event.getPlayer(), false);
        if (director == null) {
            return;
        } else {
            if (event.getAction().isLeftClick()) {
                director.saveDirection(event.getPlayer().getLocation().getDirection());
            } else {
                director.resetSavedDirection();
            }
            event.setCancelled(true);
        }
    }

}
