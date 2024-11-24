package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.craft.BaseCraft;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.datatag.CraftDataTagContainer;
import net.countercraft.movecraft.craft.datatag.CraftDataTagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;

public class CraftDirectorData {

    public static final String DATA_TAG_KEY = "movecraft_combat:director_data";

    public static final CraftDataTagKey<CraftDirectorData> KEY_DATATAG_DIRECTOR_DATA = CraftDataTagContainer.tryRegisterTagKey(new NamespacedKey("movecraft-combat", "director-data"), (craft) -> {
        return new CraftDirectorData();
    });

    public static CraftDirectorData getInstanceFor(final Craft craft) {
        return craft.getDataTag(KEY_DATATAG_DIRECTOR_DATA);
    }

    // TODO: Caches for "active" directors per type ordered by priority!
    // TODO: Cache for player->director
    // TODO: Change the entire registration to register factories, then we can set values on the directors themselves
    // TODO: Only update the target reference whenever the entity holds the item
    // TODO: How will we handle named directors in this? => Maybe only keep a list of directors with a directing entity?

    protected final List<AbstractDirector> DIRECTORS = new ArrayList<>();
    protected final WeakHashMap<String, AbstractDirector> DIRECTOR_INSTANCE_BY_NAME = new WeakHashMap<>();
    protected final WeakHashMap<UUID, AbstractDirector> ENTITY_TO_DIRECTOR = new WeakHashMap<>();
    private CraftDirectorData() {
        DirectorRegister.applyDirectorInstances(this.DIRECTORS::add, this.DIRECTOR_INSTANCE_BY_NAME::put);
    }

    public void resetDirector(Entity director) {

    }

    protected Optional<AbstractDirector> getDirectorInstance(final String type) {
        return Optional.ofNullable(DIRECTOR_INSTANCE_BY_NAME.getOrDefault(type, null));
    }

    public boolean attemptSetDirector(Entity director, String type, String details) {
        // First: Check if SOMEONE ELSE already attempts to direct this => Not allowed
        // Second: Invalidate the director that this player already has
        // Third: Create new director, references etc
        if (ENTITY_TO_DIRECTOR.containsKey(director.getUniqueId())) {
            Optional.ofNullable(ENTITY_TO_DIRECTOR.getOrDefault(director.getUniqueId(), null)).ifPresent(d -> {
                d.removeDirector(director);
                ENTITY_TO_DIRECTOR.remove(director.getUniqueId());
            });
        }
        Optional<AbstractDirector> ad = this.getDirectorInstance(type);
        if (ad.isPresent()) {
            if (ad.get().tryAddDirector(director, details)) {
                ENTITY_TO_DIRECTOR.put(director.getUniqueId(), ad.get());
            }
        }
        return false;
    }

    public boolean attemptDirect(final Entity entity) {
        for (AbstractDirector ad : DIRECTORS) {
            if (ad.attemptDirect(entity)) {
                return true;
            }
        }
        return false;
    }
}
