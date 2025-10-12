package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.utils.MathHelper;
import org.bukkit.util.Vector;

public interface IHorizontalDirector {

    public default Vector horizontalDirector_clampVector(Vector adjustedVelocity, Vector originalVelocity) {
        Vector velocity = adjustedVelocity.clone();
        velocity.setX(MathHelper.clamp(velocity.getX()));
        velocity.setY(originalVelocity.getY());
        velocity.setZ(MathHelper.clamp(velocity.getZ()));
        return velocity;
    }

}
