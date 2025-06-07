package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.SinkingCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataType;

public class DirectorHelper {

    static final NamespacedKey KEY_DIRECTOR_NODE = new NamespacedKey(MovecraftCombat.getInstance(), "director_node");
    static final NamespacedKey KEY_FLAGGED_FOR_DIRECTION = new NamespacedKey(MovecraftCombat.getInstance(), "director_needs_direction");

    public static void flagEntity(Entity entity, String value) {
        entity.getPersistentDataContainer().set(KEY_DIRECTOR_NODE, PersistentDataType.STRING, value);
    }

    public static boolean isTNTFlaggedForDirection(TNTPrimed tntPrimed) {
        return tntPrimed.getPersistentDataContainer().getOrDefault(KEY_FLAGGED_FOR_DIRECTION, PersistentDataType.BOOLEAN, false);
    }

    public static void flagTNTForDirection(TNTPrimed tntPrimed, final Craft craft) {
        if (craft == null) {
            return;
        }
        if (craft instanceof SinkingCraft) {
            return;
        }
        // TODO: Refactor to new system!
        if (!craft.getType().getBoolProperty(CannonDirectors.ALLOW_CANNON_DIRECTOR_SIGN)) {
            return;
        }
        if (isTNTFlaggedForDirection(tntPrimed)) {
            return;
        }
        tntPrimed.getPersistentDataContainer().set(KEY_FLAGGED_FOR_DIRECTION, PersistentDataType.BOOLEAN, true);
    }

    public static String getDirectorNameFrom(Entity entity) {
        if (entity.getPersistentDataContainer().isEmpty()) {
            return null;
        }

        if (entity.getPersistentDataContainer().has(KEY_DIRECTOR_NODE, PersistentDataType.STRING)) {
            return entity.getPersistentDataContainer().get(KEY_DIRECTOR_NODE, PersistentDataType.STRING);
        }
        return null;
    }

}
