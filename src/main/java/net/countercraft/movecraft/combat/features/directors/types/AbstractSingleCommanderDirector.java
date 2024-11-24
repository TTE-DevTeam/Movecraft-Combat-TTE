package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.AbstractDirector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.UUID;

public abstract class AbstractSingleCommanderDirector extends AbstractDirector {

    protected UUID commanderUUID = null;
    protected Vector commanderLookingDirection = null;

    @Override
    public boolean tryAddDirector(Entity entity, String extraData) {
        this.commanderUUID = entity.getUniqueId();
        return true;
    }

    @Override
    public void removeDirector(Entity entity) {
        if (entity.getUniqueId().equals(this.commanderUUID)) {
            this.commanderUUID = null;
        }
    }

    @Override
    public boolean hasDirectorFor(Entity entity) {
        return this.hasAnyDirector();
    }

    @Override
    public boolean hasAnyDirector() {
        return this.commanderUUID != null && Bukkit.getServer().getEntity(this.commanderUUID) != null;
    }

    protected Entity getCommanderEntity() {
        return Bukkit.getServer().getEntity(this.commanderUUID);
    }

    @Override
    protected boolean hasTargetData() {
        return this.commanderLookingDirection != null && this.commanderLookingDirection.length() > 0;
    }

}
