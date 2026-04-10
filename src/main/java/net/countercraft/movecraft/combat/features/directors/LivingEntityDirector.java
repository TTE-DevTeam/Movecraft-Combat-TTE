package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.utils.DirectorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.List;

// TODO: Store the directorObject somewhere on the entity, that is easier
public class LivingEntityDirector implements IDirectorObject {

    private WeakReference<LivingEntity> entityReference;
    private Vector aimedDirection = null;

    private static final String METADATA_KEY = "movecraft-combat_director_data";

    // Retrieves the director object for this entity from it's metadata. If absent, it is created
    public static LivingEntityDirector of(final LivingEntity entity) {
        return of(entity, true);
    }
    // Retrieves the director object for this entity from it's metadata. If createIfAbsent is set to true, it will create it
    public static LivingEntityDirector of(final LivingEntity entity, boolean createIfAbsent) {
        // TODO: Use persistent data container, if possible, but technically we dont need to remember this data as it is somewhat volatile => Use metadata, but this might be clunky
        LivingEntityDirector result = null;
        if (entity.hasMetadata(METADATA_KEY)) {
            List<MetadataValue> metadata = entity.getMetadata(METADATA_KEY);
            for (MetadataValue value : metadata) {
                if (value instanceof FixedMetadataValue fmv) {
                    final Object fmvValue = fmv.value();
                    if (fmvValue instanceof LivingEntityDirector led) {
                        result = led;
                        break;
                    }
                }
            }
        }
        if (result == null && createIfAbsent) {
            result = new LivingEntityDirector(entity);
            unsetData(entity);
            entity.setMetadata(METADATA_KEY, new FixedMetadataValue(MovecraftCombat.getInstance(), result));
        }
        return result;
    }

    public static void unsetData(final LivingEntity entity) {
        entity.removeMetadata(METADATA_KEY, MovecraftCombat.getInstance());
    }

    private LivingEntityDirector(final LivingEntity entity) {
        this.entityReference = new WeakReference<>(entity);
    }

    protected boolean isStillValid() {
        LivingEntity entity = this.entityReference.get();
        if (entity == null || entity.isDead() || !entity.isValid()) {
            return false;
        }
        if (entity instanceof Mob mob) {
            return (mob.getEquipment().getItemInMainHand().getType() == Directors.DirectorTool) || (this.aimedDirection != null);
        }
        if (entity instanceof Player player) {
            return player.isOnline() && ((player.getEquipment().getItemInMainHand().getType() == Directors.DirectorTool) || (this.aimedDirection != null));
        } else {
            return true;
        }
    }

    public Vector getDirectionVector(int convergenceDistance, Vector projectileLocation) {
        if (!this.isStillValid()) {
            return null;
        }
        Vector result = this.aimedDirection;
        final LivingEntity entity = this.entityReference.get();
        if (entity != null) {
            if (entity.getEquipment().getItemInMainHand().getType() == Directors.DirectorTool) {
                result = entity.getLocation().getDirection();
            }
        }
        if (entity instanceof Player player) {
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
    public void saveDirection(Vector direction) {
        if (direction != null) {
            this.aimedDirection = direction;
        }
    }

    @Override
    public void resetSavedDirection() {
        this.aimedDirection = null;
    }

    @Override
    public void sendMessage(Component message) {
        if (this.entityReference.get() != null) {
            this.entityReference.get().sendMessage(message);
        }
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

    @Override
    public void onRemoved() {
        IDirectorObject.super.onRemoved();

        final LivingEntity entity = this.entityReference.get();
        if (entity != null) {
            unsetData(entity);
        }
    }
}
