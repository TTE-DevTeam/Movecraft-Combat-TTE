package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class DirectorHelper {

    static final NamespacedKey DIRECTOR_HINT = new NamespacedKey(MovecraftCombat.getInstance(), "director_node");

    public static void flagEntity(Entity entity, String value) {
        entity.getPersistentDataContainer().set(DIRECTOR_HINT, PersistentDataType.STRING, value);
    }

    public static String getDirectorNameFrom(Entity entity) {
        if (entity.getPersistentDataContainer().isEmpty()) {
            return null;
        }

        if (entity.getPersistentDataContainer().has(DIRECTOR_HINT, PersistentDataType.STRING)) {
            return entity.getPersistentDataContainer().get(DIRECTOR_HINT, PersistentDataType.STRING);
        }
        return null;
    }

}
