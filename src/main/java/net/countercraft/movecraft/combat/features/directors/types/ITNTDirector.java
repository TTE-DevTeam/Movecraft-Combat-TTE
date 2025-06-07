package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;

import java.util.function.Function;

public interface ITNTDirector {

    public default Triple<String, NamespacedKey, Function<CraftType, Double>> tntDirector_getMaxAngleCraftTypeDoubleProperty() {
        return Triple.of("maxTNTDirectorAngle", new NamespacedKey("movecraft-combat", "max_tnt_director_angle"), c -> 60.0D);
    }

    public default Triple<String, NamespacedKey, Function<CraftType, Boolean>> tntDirector_getAllowedOnCraftCraftTypeBooleanProperty() {
        return Triple.of("allowTNTDirector", new NamespacedKey("movecraft-combat", "allow_tnt_director"), c -> false);
    }

}
