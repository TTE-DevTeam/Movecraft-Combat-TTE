package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.SinkingCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class DirectorHelper {

    static final NamespacedKey KEY_WAS_DIRECTED_AGAIN = new NamespacedKey(MovecraftCombat.getInstance(), "directed_twice");
    static final NamespacedKey KEY_DIRECTION_TIMESTAMP = new NamespacedKey(MovecraftCombat.getInstance(), "director_timestamp");
    static final NamespacedKey KEY_DIRECTING_CRAFT = new NamespacedKey(MovecraftCombat.getInstance(), "directing_craft");
    static final NamespacedKey KEY_DIRECTOR_NODE = new NamespacedKey(MovecraftCombat.getInstance(), "director_node");
    static final NamespacedKey KEY_FLAGGED_FOR_DIRECTION = new NamespacedKey(MovecraftCombat.getInstance(), "director_needs_direction");

    public static void flagEntity(Entity entity, String value) {
        entity.getPersistentDataContainer().set(KEY_DIRECTOR_NODE, PersistentDataType.STRING, value);
    }

    public static boolean isTNTFlaggedForDirection(TNTPrimed tntPrimed) {
        return tntPrimed.getPersistentDataContainer().getOrDefault(KEY_FLAGGED_FOR_DIRECTION, PersistentDataType.BOOLEAN, false);
    }

    public static void flagProjectileAsDirected(Projectile projectile, Craft craft, boolean directedTwice) {
        if (!directedTwice) {
            projectile.getPersistentDataContainer().set(KEY_DIRECTION_TIMESTAMP, PersistentDataType.INTEGER, projectile.getTicksLived());
            projectile.getPersistentDataContainer().set(KEY_DIRECTING_CRAFT, PersistentDataType.STRING, craft.getUUID().toString());
        }
        projectile.getPersistentDataContainer().set(KEY_WAS_DIRECTED_AGAIN, PersistentDataType.BOOLEAN, directedTwice);
    }

    public static boolean canBeDirectedAgain(Projectile projectile, final Craft craft) {
        if (projectile.getPersistentDataContainer().has(KEY_WAS_DIRECTED_AGAIN, PersistentDataType.BOOLEAN) && projectile.getPersistentDataContainer().get(KEY_WAS_DIRECTED_AGAIN, PersistentDataType.BOOLEAN)) {
            return false;
        }
        final double distanceToOrigin = projectile.getLocation().distanceSquared(projectile.getOrigin());
        if (distanceToOrigin > 9.0D) {
            return false;
        }
        if (projectile.getPersistentDataContainer().has(KEY_DIRECTION_TIMESTAMP, PersistentDataType.INTEGER) && projectile.getPersistentDataContainer().has(KEY_DIRECTING_CRAFT, PersistentDataType.STRING)) {
            int ageAtDirectionTime = projectile.getPersistentDataContainer().get(KEY_DIRECTION_TIMESTAMP, PersistentDataType.INTEGER);
            UUID directingCraft = UUID.fromString(projectile.getPersistentDataContainer().get(KEY_DIRECTING_CRAFT, PersistentDataType.STRING));

            if (craft == null || !craft.getUUID().equals(directingCraft)) {
                return false;
            } else {
                int passedTimeSinceDirecting = projectile.getTicksLived() - ageAtDirectionTime;
                return passedTimeSinceDirecting <= 10;
            }
        }
        return false;
    }

    public static void flagTNTForDirection(TNTPrimed tntPrimed, final Craft craft) {
        if (craft == null) {
            return;
        }
        if (craft instanceof SinkingCraft) {
            return;
        }
        // TODO: Refactor to new system!
        if (!craft.getCraftProperties().get(CannonDirectors.ALLOW_CANNON_DIRECTOR_PROPERTY)) {
            return;
        }
        if (isTNTFlaggedForDirection(tntPrimed)) {
            return;
        }
        tntPrimed.getPersistentDataContainer().set(KEY_FLAGGED_FOR_DIRECTION, PersistentDataType.BOOLEAN, true);
    }

    public static String getDirectorNameFrom(Entity entity) {
        if (entity.getPersistentDataContainer().isEmpty()) {
            return null;
        }

        if (entity.getPersistentDataContainer().has(KEY_DIRECTOR_NODE, PersistentDataType.STRING)) {
            return entity.getPersistentDataContainer().get(KEY_DIRECTOR_NODE, PersistentDataType.STRING);
        }
        return null;
    }

}
