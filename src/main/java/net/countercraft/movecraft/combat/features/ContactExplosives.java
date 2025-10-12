package net.countercraft.movecraft.combat.features;

import de.dertoaster.extraevents.api.event.TNTHitEvent;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ContactExplosives implements Listener {
    public static boolean EnableContactExplosives = true;
    public static double ContactExplosivesMinImpuse = 0.35D;
    private final Object2DoubleOpenHashMap<TNTPrimed> tracking = new Object2DoubleOpenHashMap<>();
    private long lastCheck = 0;

    public static void load(@NotNull FileConfiguration config) {
        EnableContactExplosives = config.getBoolean("EnableContactExplosives", true);
        ContactExplosivesMinImpuse = config.getDouble("ContactExplosivesMinImpuse", 0.35D);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTNTImpact(TNTHitEvent event) {
        if (!EnableContactExplosives) {
            return;
        }
        Vector velocity = event.getEntity().getVelocity();
        if (velocity.lengthSquared() < ContactExplosivesMinImpuse) {
            return;
        }
        event.getEntity().setVelocity(new Vector(0, 0, 0)); //freeze it in place to prevent sliding
        event.getEntity().setFuseTicks(0);
    }
    
}
