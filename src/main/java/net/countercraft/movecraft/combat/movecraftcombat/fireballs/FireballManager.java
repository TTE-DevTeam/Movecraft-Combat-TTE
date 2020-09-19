package net.countercraft.movecraft.combat.movecraftcombat.fireballs;

import net.countercraft.movecraft.combat.movecraftcombat.MovecraftCombat;
import net.countercraft.movecraft.combat.movecraftcombat.config.Config;
import net.countercraft.movecraft.combat.movecraftcombat.tracking.FireballTracking;
import org.bukkit.entity.SmallFireball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

public class FireballManager extends BukkitRunnable {
    private static FireballManager instance;
    private LinkedList<SmallFireball> q;

    public static FireballManager getInstance() {
        return instance;
    }

    public FireballManager() {
        instance = this;
        q = new LinkedList<>();
    }

    @Override
    public void run() {
        int timeLimit = 20 * Config.FireballLifespan * 50;

        while(System.currentTimeMillis() - q.peek().getMetadata("MCC-Expiry").get(0).asLong() > timeLimit && q.size() > 0) {
            SmallFireball f = q.pop();
            f.remove();
            FireballTracking.getInstance().expiredFireball(f);
        }
    }

    public void addFireball(SmallFireball f) {
        f.setMetadata("MCC-Expiry", new FixedMetadataValue(MovecraftCombat.getInstance(), System.currentTimeMillis()));
        q.add(f);
    }
}
