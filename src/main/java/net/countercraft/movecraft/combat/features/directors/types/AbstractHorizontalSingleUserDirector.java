package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.utils.MathHelper;
import org.bukkit.util.Vector;

import java.util.Map;

public abstract class AbstractHorizontalSingleUserDirector extends AbstractSingleUserDirector {

    public AbstractHorizontalSingleUserDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    protected Vector clampVector(Vector adjustedVelocity, Vector originalVelocity) {
        Vector velocity = adjustedVelocity.clone();
        velocity.setX(MathHelper.clamp(velocity.getX()));
        velocity.setY(originalVelocity.getY());
        velocity.setZ(MathHelper.clamp(velocity.getZ()));
        return velocity;
    }

}
