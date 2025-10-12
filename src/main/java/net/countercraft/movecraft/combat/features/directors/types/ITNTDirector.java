package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

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

    public default Triple<String, NamespacedKey, Function<CraftType, Double>> tntDirector_getMaxAngleCraftTypeDoubleProperty() {
        return Triple.of("maxTNTDirectorAngle", new NamespacedKey("movecraft-combat", "max_tnt_director_angle"), c -> 60.0D);
    }

    public default Triple<String, NamespacedKey, Function<CraftType, Boolean>> tntDirector_getAllowedOnCraftCraftTypeBooleanProperty() {
        return Triple.of("allowTNTDirector", new NamespacedKey("movecraft-combat", "allow_tnt_director"), c -> false);
    }

}
