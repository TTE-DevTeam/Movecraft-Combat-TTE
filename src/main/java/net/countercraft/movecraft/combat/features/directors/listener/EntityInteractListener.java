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
        if (!event.getAction().isLeftClick()) {
            return;
        }
        final Material item = event.getItem().getType();

        if (item != Directors.DirectorTool) {
            return;
        }

        // TODO: Find the directors for this player and save the vector
        LivingEntityDirector director = LivingEntityDirector.of(event.getPlayer(), false);
        if (director == null) {
            return;
        } else {
            director.saveDirection(event.getPlayer().getLocation().getDirection());
            event.setCancelled(true);
        }
    }

}
