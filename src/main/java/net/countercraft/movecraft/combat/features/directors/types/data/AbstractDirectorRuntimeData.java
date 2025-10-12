package net.countercraft.movecraft.combat.features.directors.types.data;

import net.countercraft.movecraft.combat.features.directors.IDirectorObject;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class AbstractDirectorRuntimeData {

    public abstract boolean hasAnyDirector();
    protected abstract IDirectorObject getDirector(final Entity toDirect);

    public Vector getDirectionVectorFor(final Entity toDirect, int convergenceDistance) {
        IDirectorObject directorObject = this.getDirector(toDirect);
        if (directorObject != null) {
            return directorObject.getDirectionVector(convergenceDistance, toDirect.getLocation().toVector());
        }
        return null;
    }

    public abstract boolean addDirector(final IDirectorObject director, String... args);
    public abstract boolean removeDirector(final IDirectorObject director);

}
