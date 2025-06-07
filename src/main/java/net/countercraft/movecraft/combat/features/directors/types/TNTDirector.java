package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class TNTDirector extends AbstractSingleUserDirector implements IHorizontalDirector {

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
    protected Vector clampVector(Vector adjustedVelocity, Vector originalVelocity) {
        return horizontalDirector_clampVector(adjustedVelocity, originalVelocity);
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
