package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.PropertyKey;
import net.countercraft.movecraft.craft.type.PropertyKeyTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ITNTDirector {

    public void setFuseTime(int value);
    public int getFuseTime();

    public default void tntDirector_deserialize(Map<String, Object> rawData) {
        this.setFuseTime(NumberConversions.toInt(rawData.getOrDefault("fuseTime", this.getFuseTime())));
    }

    public default @NotNull Map<String, Object> tntDirector_addToSerialize(@NotNull Map<String, Object> serialized) {
        serialized.putAll(Map.of(
                "fuseTime", this.getFuseTime()
        ));

        return serialized;
    }

    public default void tntDirector_applyVelocity(@NotNull Craft craft, Entity entity, Vector supposedVelocity) {
        if (entity instanceof TNTPrimed tntPrimed) {
            if (this.getFuseTime() > 0) {
                tntPrimed.setFuseTicks(this.getFuseTime());
            }
        }
    }

    public default PropertyKey<Double> tntDirector_getMaxAngleCraftTypeDoubleProperty() {
        return PropertyKeyTypes.doublePropertyKey(new NamespacedKey("movecraft-combat", "max_tnt_director_angle"), 60.0D);
    }

    public default PropertyKey<Boolean> tntDirector_getAllowedOnCraftCraftTypeBooleanProperty() {
        return PropertyKeyTypes.boolPropertyKey(new NamespacedKey("movecraft-combat", "allow_tnt_director"), false);
    }

}
