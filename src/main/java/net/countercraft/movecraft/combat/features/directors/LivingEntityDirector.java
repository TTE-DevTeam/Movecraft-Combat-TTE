package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.utils.DirectorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

public class LivingEntityDirector implements IDirectorObject {

    private WeakReference<LivingEntity> entityReference;

    public LivingEntityDirector(final LivingEntity entity) {
        this.entityReference = new WeakReference<>(entity);
    }

    protected boolean isStillValid() {
        LivingEntity entity = this.entityReference.get();
        if (entity == null || entity.isDead() || !entity.isValid()) {
            return false;
        }
        if (entity instanceof Mob mob) {
            return mob.getEquipment().getItemInMainHand().getType() == Directors.DirectorTool;
        }
        if (entity instanceof Player player) {
            return player.isOnline() && player.getEquipment().getItemInMainHand().getType() == Directors.DirectorTool;
        } else {
            return true;
        }
    }

    public Vector getDirectionVector(int convergenceDistance, Vector projectileLocation) {
        if (!this.isStillValid()) {
            return null;
        }
        Vector result = this.entityReference.get().getLocation().getDirection();
        if (this.entityReference.get() instanceof Player player) {
            // If the player is actively using an item (or is sneaking), then we can try to converge
            if (convergenceDistance >= 0 && (player.hasActiveItem() || player.isSneaking())) {
                Block targetBlock = DirectorUtils.getDirectorBlock(this.entityReference.get(), convergenceDistance);
                if (targetBlock != null && !targetBlock.getType().isAir()) {
                    result = targetBlock.getLocation().toVector().subtract(projectileLocation);
                    result.normalize();
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof LivingEntityDirector other) {
            LivingEntity otherEntity = other.entityReference.get();
            LivingEntity selfEntity = this.entityReference.get();
            if (otherEntity == selfEntity) {
                return true;
            }
            if (otherEntity == null || selfEntity == null) {
                return false;
            }
            return otherEntity.getUniqueId().equals(selfEntity.getUniqueId());
        }
        return false;
    }
}
