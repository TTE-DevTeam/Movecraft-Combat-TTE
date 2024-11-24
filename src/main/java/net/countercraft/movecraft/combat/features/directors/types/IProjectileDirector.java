package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.VectorHelper;
import org.bukkit.Axis;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public interface IProjectileDirector {

    public static final Map<EntityType, Double> SPEED_MAPPING = new HashMap<>();
    public static final double DEFAULT_SPEED = 1;

    public static final float MAX_ANGLE_X = 60.0F;
    public static final float MAX_ANGLE_Y = 60.0F;
    public static final float MAX_ANGLE_Z = 60.0F;

    public static double getSpeedFor(EntityType type) {
        return SPEED_MAPPING.computeIfAbsent(type, t -> DEFAULT_SPEED);
    }

    public Entity getCommanderEntity(Entity toBeDirected);
    public Vector getTargetDirection(Entity toBeDirected, Entity commander);

    public default boolean shouldDirect(Entity entity) {
        return entity instanceof Projectile;
    }

    public default void doDirect(Entity entity) {
        Projectile proj = (Projectile) entity;
        Entity commander = this.getCommanderEntity(entity);

        if (commander != null && commander instanceof ProjectileSource) {
            proj.setShooter((ProjectileSource) commander);
        }

        final double speed = getSpeedFor(proj.getType());
        // Check if the vectors don't differ too much, then apply them!
        Vector v = this.getTargetDirection(entity, commander).clone().normalize();
        final Vector oldPath = proj.getVelocity().clone().normalize();

        // Attention: For fireball derivates you also need to call setDirection!
        float angleX = Math.abs(VectorHelper.getAngleBetween(oldPath, v, Axis.X));
        float angleY = Math.abs(VectorHelper.getAngleBetween(oldPath, v, Axis.Y));
        float angleZ = Math.abs(VectorHelper.getAngleBetween(oldPath, v, Axis.Z));
        if (angleX <= MAX_ANGLE_X && angleY <= MAX_ANGLE_Y && angleZ <= MAX_ANGLE_Z) {
            v = v.multiply(speed);

            proj.setVelocity(v);
            if (proj instanceof Fireball) {
                ((Fireball)proj).setDirection(v);
            }
        }

    }
}
