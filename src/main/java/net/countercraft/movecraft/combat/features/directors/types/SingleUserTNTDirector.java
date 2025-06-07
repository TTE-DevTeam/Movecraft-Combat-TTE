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

public class SingleUserTNTDirector extends AbstractSingleUserDirector implements IHorizontalDirector, ITNTDirector {

    private int fuseTime;

    public SingleUserTNTDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    protected void deserialize(Map<String, Object> rawData) {
        tntDirector_deserialize(rawData);
    }

    @Override
    public @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized) {
        return tntDirector_addToSerialize(serialized);
    }

    @Override
    protected void applyVelocity(@NotNull Craft craft, Entity entity, Vector supposedVelocity) {
        super.applyVelocity(craft, entity, supposedVelocity);
        tntDirector_applyVelocity(craft, entity, supposedVelocity);
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

    @Override
    public void setFuseTime(int value) {
        this.fuseTime = value;
    }

    @Override
    public int getFuseTime() {
        return this.fuseTime;
    }
}
