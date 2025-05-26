package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.features.directors.types.AbstractDirector;
import net.countercraft.movecraft.combat.features.directors.types.data.AbstractDirectorRuntimeData;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CraftDirectorData {

    private final Map<AbstractDirector<? extends AbstractDirectorRuntimeData>, AbstractDirectorRuntimeData> DIRECTOR_DATA = new WeakHashMap<>();
    private final WeakReference<Craft> craftReference;
    private final Map<EntityType, List<AbstractDirector>> allowedDirectors;

    private <T extends AbstractDirectorRuntimeData> T getRunTimeData(AbstractDirector<T> director) {
        Object backed = DIRECTOR_DATA.computeIfAbsent(director, k -> k.createRuntimeData());
        try {
            T result = (T) backed;
            return result;
        } catch(ClassCastException cce) {
            throw new IllegalStateException("Stored object for director <" + director.toString() +"> does not have the correct type!");
        }
    }

    public static CraftDirectorData get(final Craft craft) {
        return craft.getDataTag(Directors.DATA_TAG_KEY_DIRECTOR_DATA);
    }

    public CraftDirectorData(final Craft craft) {
        this.craftReference = new WeakReference(craft);
        this.allowedDirectors = new HashMap<>(Directors.DIRECTORS_PER_TYPE);
        for (List<AbstractDirector> list : allowedDirectors.values()) {
            list.removeIf(ad -> !ad.allowedOnCraft(craft));
        }
        this.allowedDirectors.values().removeIf(List::isEmpty);
    }

    public boolean hasAnyDirector() {
        for (AbstractDirectorRuntimeData adrd : DIRECTOR_DATA.values()) {
            if (adrd.hasAnyDirector())
                return true;
        }
        return false;
    }

    public boolean attemptDirectEntity(Entity entity) {
        if (this.craftReference.get() == null) {
            return false;
        }
        // First, check if there are any directors present
        // Second, loop over the ordered directors for that entity type
        // Third, attempt on each director to direct that entity
        if (!this.hasAnyDirector()) {
            return false;
        }
        List<AbstractDirector> orderedDirectors = this.allowedDirectors.getOrDefault(entity.getType(), null);
        if (orderedDirectors == null || orderedDirectors.isEmpty()) {
            return false;
        }
        for (AbstractDirector potentialDirector : orderedDirectors) {
            if (potentialDirector.attemptDirectEntity(this.craftReference.get(), entity, getRunTimeData(potentialDirector))) {
                return true;
            }
        }
        return false;
    }
}
