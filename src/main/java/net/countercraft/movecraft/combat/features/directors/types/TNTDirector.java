package net.countercraft.movecraft.combat.features.directors.types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

public class TNTDirector extends AbstractSingleCommanderDirector {
    @Override
    public boolean shouldDirect(Entity entity) {
        return entity instanceof TNTPrimed;
    }

    @Override
    protected void doDirect(Entity entity) {

    }
}
