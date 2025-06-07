package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class TNTDirector extends AbstractHorizontalSingleUserDirector {

    public TNTDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    protected void deserialize(Map<String, Object> rawData) {
        // we dont have custom data, ignore it!
    }

    @Override
    public @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized) {
        return Map.of();
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Double>> getMaxAngleCraftTypeDoubleProperty() {
        return Triple.of("maxTNTDirectorAngle", new NamespacedKey("movecraft-combat", "max_tnt_director_angle"), c -> 60.0D);
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Boolean>> getAllowedOnCraftCraftTypeBooleanProperty() {
        return Triple.of("allowTNTDirector", new NamespacedKey("movecraft-combat", "allow_tnt_director"), c -> false);
    }
}
