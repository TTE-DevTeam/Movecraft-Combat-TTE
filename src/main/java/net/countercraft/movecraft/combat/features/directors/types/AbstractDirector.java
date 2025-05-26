package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.AbstractDirectorRuntimeData;
import net.countercraft.movecraft.combat.features.directors.types.sign.AbstractDirectorSign;
import net.countercraft.movecraft.combat.utils.DirectorUtils;
import net.countercraft.movecraft.combat.utils.MathHelper;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.property.BooleanProperty;
import net.countercraft.movecraft.craft.type.property.DoubleProperty;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractDirector<T extends AbstractDirectorRuntimeData> implements ConfigurationSerializable, Comparable<AbstractDirector> {

    private final int priority;
    private final Set<EntityType> ENTITY_TYPES = new HashSet<>();
    private final NamespacedKey allowedOnCraftKey;
    private final NamespacedKey maxAngleKey;

    protected final double velocityModifier;
    protected final double maxAngle;
    protected final int convergenceDistance;

    public AbstractDirector(Map<String, Object> rawData) {
        this.priority = NumberConversions.toInt(rawData.getOrDefault("Priority", 1));
        // Constructor necessary for object deserialization from config

        if (this.priority <= 0) {
            throw new IllegalArgumentException("thresholdMultiplier must be greater than 0.");
        }

        this.velocityModifier = NumberConversions.toDouble(rawData.getOrDefault("VelocityMultiplier", 1.0));
        this.maxAngle = NumberConversions.toDouble(rawData.getOrDefault("MaxDirectorAngle", 45.0));
        this.convergenceDistance = NumberConversions.toInt(rawData.getOrDefault("ConvergenceRange", -1));

        Object storedListObj = rawData.getOrDefault("EntityTypes", List.of());
        try {
            List<String> listTmp = (List<String>) storedListObj;
            for (String s : listTmp) {
                try {
                    EntityType type = EntityType.valueOf(s);
                    if (type != null) {
                        ENTITY_TYPES.add(type);
                    }
                } catch(IllegalArgumentException iae) {
                    continue;
                }
            }
        } catch(ClassCastException cce) {
            throw new IllegalArgumentException("Entity types is not a list!");
        }

        if (this.getAllowedOnCraftCraftTypeBooleanProperty() != null) {
            this.allowedOnCraftKey = this.getAllowedOnCraftCraftTypeBooleanProperty().getMiddle();
        } else {
            this.allowedOnCraftKey = null;
        }

        if (this.getMaxAngleCraftTypeDoubleProperty() != null) {
            this.maxAngleKey = this.getMaxAngleCraftTypeDoubleProperty().getMiddle();
        } else {
            this.maxAngleKey = null;
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = Map.of(
                "Threshold", this.priority,
                "EntityTypes", this.ENTITY_TYPES.stream().toList(),
                "VelocityMultiplier", this.velocityModifier,
                "MaxDirectorAngle", this.maxAngle
        );
        return addToSerialize(serialized);
    }

    public abstract @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized);

    @Override
    public int compareTo(@NotNull AbstractDirector other) {
        return Integer.compare(this.priority, other.priority);
    }

    public abstract T createRuntimeData();

    protected void applyVelocity(final @NotNull Craft craft, final Entity entity, final Vector supposedVelocity) {
        Vector velocity = entity.getVelocity();
        double originalSpeed = velocity.length();
        velocity.normalize();
        velocity = DirectorUtils.limitVectorToMaxAngle(supposedVelocity, velocity, this.getMaxAngle(craft));

        velocity.multiply(originalSpeed * this.velocityModifier); // put the original speed back in, but now along a different trajectory

        velocity = clampVector(velocity, entity.getVelocity());

        try {
            velocity.checkFinite();
        }
        catch (IllegalArgumentException ignored) {
            return;
        }
        entity.setVelocity(velocity);
    }

    protected Vector clampVector(Vector adjustedVelocity, Vector originalVelocity) {
        Vector velocity = adjustedVelocity.clone();
        velocity.setX(MathHelper.clamp(velocity.getX()));
        velocity.setY(MathHelper.clamp(velocity.getY()));
        velocity.setZ(MathHelper.clamp(velocity.getZ()));
        return velocity;
    }

    public EntityType[] getEntityTypes() {
        return this.ENTITY_TYPES.toArray(new EntityType[this.ENTITY_TYPES.size()]);
    }

    public boolean attemptDirectEntity(@NotNull Craft craft, Entity entity, T runTimeData) {
        if (!runTimeData.hasAnyDirector()) {
            return false;
        }
        if (!this.allowedOnCraft(craft)) {
            return false;
        }
        Vector direction = runTimeData.getDirectionVectorFor(entity, this.convergenceDistance);
        if (direction == null) {
            return false;
        }
        this.applyVelocity(craft, entity, direction);

        return false;
    }

    public boolean allowedOnCraft(@NotNull Craft craft) {
        if (this.allowedOnCraftKey != null) {
            return craft.getType().getBoolProperty(this.allowedOnCraftKey);
        }
        return true;
    }

    protected double getMaxAngle(@NotNull Craft craft) {
        if (this.maxAngleKey != null) {
            return craft.getType().getDoubleProperty(this.maxAngleKey);
        } else {
            return this.maxAngle;
        }
    }

    @Nullable
    public AbstractDirectorSign createDirectorSignHandler() {
        return null;
    }

    protected abstract Triple<String, NamespacedKey, Function<CraftType, Double>> getMaxAngleCraftTypeDoubleProperty();
    protected abstract Triple<String, NamespacedKey, Function<CraftType, Boolean>> getAllowedOnCraftCraftTypeBooleanProperty();

    public void registerCraftTypeProperties() {
        Triple<String, NamespacedKey, Function<CraftType, Double>> angleProperty = this.getMaxAngleCraftTypeDoubleProperty();
        if (angleProperty != null) {
            CraftType.registerProperty(new DoubleProperty(angleProperty.getLeft(), angleProperty.getMiddle(), angleProperty.getRight()));
        }
        Triple<String, NamespacedKey, Function<CraftType, Boolean>> allowedProperty = this.getAllowedOnCraftCraftTypeBooleanProperty();
        if (angleProperty != null) {
            CraftType.registerProperty(new BooleanProperty(allowedProperty.getLeft(), allowedProperty.getMiddle(), allowedProperty.getRight()));
        }
    }

}
