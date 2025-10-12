package net.countercraft.movecraft.combat.features.directors.types.data;

import net.countercraft.movecraft.combat.features.directors.IDirectorObject;
import org.bukkit.entity.Entity;

public class SingleUserDirectorRuntimeData extends AbstractDirectorRuntimeData {

    public IDirectorObject directorObject;

    @Override
    public boolean hasAnyDirector() {
        return this.directorObject != null;
    }

    @Override
    protected IDirectorObject getDirector(Entity toDirect) {
        return this.directorObject;
    }

    @Override
    public boolean addDirector(IDirectorObject director, String... args) {
        this.directorObject = director;
        return true;
    }

    @Override
    public boolean removeDirector(IDirectorObject director) {
        if (this.directorObject != null) {
            if (this.directorObject.equals(director)) {
                this.directorObject = null;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
