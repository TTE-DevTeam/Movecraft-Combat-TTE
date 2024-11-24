package net.countercraft.movecraft.combat.features.directors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.function.Function;

public abstract class AbstractDirector {

    public record DirectorConstructor(String name, int priority, Function<String, AbstractDirector> constructor) {
        public AbstractDirector create() {
                return this.constructor.apply(this.name);
            }
        }

    public abstract boolean tryAddDirector(Entity entity, String extraData);
    public abstract void removeDirector(Entity entity);
    public abstract boolean hasDirectorFor(Entity entity);
    public abstract boolean hasAnyDirector();

    public abstract boolean shouldDirect(Entity entity);
    protected abstract boolean hasTargetData();

    protected abstract void doDirect(Entity entity);

    public boolean attemptDirect(Entity entity) {
        if (!this.shouldDirect(entity)) {
            return false;
        }
        if (!this.hasAnyDirector()) {
            return false;
        }
        if (!this.hasDirectorFor(entity)) {
            return false;
        }
        if (!this.hasTargetData()) {
            return false;
        }

        this.doDirect(entity);

        return true;
    }

}
