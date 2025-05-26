package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class TNTDirector extends HorizontalSingleUserDirector {

    public TNTDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    public @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized) {
        return Map.of();
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Double>> getMaxAngleCraftTypeDoubleProperty() {
        return null;
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Boolean>> getAllowedOnCraftCraftTypeBooleanProperty() {
        return null;
    }
}
