package net.countercraft.movecraft.combat.features.directors.types.data;

import net.countercraft.movecraft.combat.features.directors.DirectorHelper;
import net.countercraft.movecraft.combat.features.directors.IDirectorObject;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class MultiUserDirectorRuntimeData extends AbstractDirectorRuntimeData {

    protected Map<String, IDirectorObject> directors = new HashMap<>();

    @Override
    public boolean hasAnyDirector() {
        return !this.directors.values().isEmpty();
    }

    @Override
    protected IDirectorObject getDirector(Entity toDirect) {
        String nodeIdent = DirectorHelper.getDirectorNameFrom(toDirect);
        if (nodeIdent == null || nodeIdent.isBlank()) {
            return null;
        }
        return this.directors.getOrDefault(nodeIdent, null);
    }

    @Override
    public boolean addDirector(IDirectorObject director, String... args) {
        if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
            return false;
        }
        this.directors.put(args[0], director);
        return true;
    }

    @Override
    public boolean removeDirector(IDirectorObject director) {
        return this.directors.values().remove(director);
    }
}
