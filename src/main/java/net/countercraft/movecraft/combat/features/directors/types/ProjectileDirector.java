package net.countercraft.movecraft.combat.features.directors.types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class ProjectileDirector extends AbstractSingleCommanderDirector implements IProjectileDirector {

    @Override
    public Entity getCommanderEntity(Entity toBeDirected) {
        return this.getCommanderEntity();
    }

    @Override
    public Vector getTargetDirection(Entity toBeDirected, Entity commander) {
        return this.commanderLookingDirection;
    }

    @Override
    public boolean shouldDirect(Entity entity) {
        return IProjectileDirector.super.shouldDirect(entity);
    }

    @Override
    public void doDirect(Entity entity) {
        IProjectileDirector.super.doDirect(entity);
    }


}
