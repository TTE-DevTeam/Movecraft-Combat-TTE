package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.MultiUserDirectorRuntimeData;
import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class MultiUserTNTDirector extends AbstractMultiUserDirector<MultiUserDirectorRuntimeData> implements IHorizontalDirector, ITNTDirector {

    public MultiUserTNTDirector(Map<String, Object> rawData) {
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
    public MultiUserDirectorRuntimeData createRuntimeData() {
        return new MultiUserDirectorRuntimeData();
    }

    @Override
    protected Vector clampVector(Vector adjustedVelocity, Vector originalVelocity) {
        return horizontalDirector_clampVector(adjustedVelocity, originalVelocity);
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Double>> getMaxAngleCraftTypeDoubleProperty() {
        return tntDirector_getMaxAngleCraftTypeDoubleProperty();
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Boolean>> getAllowedOnCraftCraftTypeBooleanProperty() {
        return tntDirector_getAllowedOnCraftCraftTypeBooleanProperty();
    }
}
