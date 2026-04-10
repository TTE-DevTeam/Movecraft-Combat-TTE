package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.utils.ConfigHelper;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.PropertyKey;
import net.countercraft.movecraft.craft.type.PropertyKeyTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IProjectileDirector {

    public void setDisableGravity(boolean value);
    public boolean getDisableGravity();

    public void setAllowedOnCraftKey(NamespacedKey value);
    public NamespacedKey getALlowedOnCraftKey();

    public void setMaxAngleKey(NamespacedKey value);
    public NamespacedKey getMaxAngleKey();

    public void setDefaultMaxAngle(double value);
    public double getDefaultMaxAngle();

    public void setDefaultAllowedOnCraft(boolean value);
    public boolean getDefaultAllowedOnCraft();

    public void setVisualItemStack(ItemStack value);
    public ItemStack getVisualItemStack();


    public default void projectileDirector_deserialize(Map<String, Object> rawData) {
        this.setDisableGravity(ConfigHelper.readBoolean(rawData, "disableGravity", false));

        this.setAllowedOnCraftKey(ConfigHelper.readNamespacedKey(rawData, "allowedOnCraftKey", new NamespacedKey("movecraft-combat", "allowProjectileDirector")));
        this.setDefaultAllowedOnCraft(ConfigHelper.readBoolean(rawData, "allowedOnCraftDefaultValue", this.getDefaultAllowedOnCraft()));

        this.setMaxAngleKey(ConfigHelper.readNamespacedKey(rawData, "maxAngleCraftKey", new NamespacedKey("movecraft-combat", "maxProjectileDirectorAngle")));
        this.setDefaultMaxAngle(NumberConversions.toDouble(rawData.getOrDefault("maxProjectileDirectorAngleDefaultValue", this.getDefaultMaxAngle())));

        Object itemStack = rawData.getOrDefault("visualItemStack", null);
        if (itemStack != null && itemStack instanceof ItemStack) {
            setVisualItemStack((ItemStack)itemStack);
        }
    }

    public default @NotNull Map<String, Object> projectileDirector_addToSerialize(@NotNull Map<String, Object> serialized) {
        serialized.putAll(Map.of(
                "disableGravity", this.getDisableGravity(),
                "allowedOnCraftKey", this.getALlowedOnCraftKey(),
                "allowedOnCraftDefaultValue", this.getDefaultAllowedOnCraft(),
                "maxAngleCraftKey", this.getMaxAngleKey(),
                "maxProjectileDirectorAngleDefaultValue", this.getDefaultMaxAngle(),
                "visualItemStack", this.getVisualItemStack()
        ));

        return serialized;
    }

    public default void projectileDirector_applyVelocity(@NotNull Craft craft, Entity entity, Vector supposedVelocity) {
        entity.setGravity(this.getDisableGravity());

        if (entity instanceof Projectile projectile) {
            // TODO: Set shooter??
        }

        if (entity instanceof ThrowableProjectile throwableProjectile) {
            if (this.getVisualItemStack() != null && !this.getVisualItemStack().isEmpty()) {
                throwableProjectile.setItem(this.getVisualItemStack().clone());
            }
        }
    }

    public default PropertyKey<Boolean> projectileDirector_getAllowedOnCraftCraftTypeBooleanProperty() {
        return PropertyKeyTypes.boolPropertyKey(this.getALlowedOnCraftKey(), this.getDefaultAllowedOnCraft());
    }

    public default PropertyKey<Double> projectileDirector_getMaxAngleCraftTypeDoubleProperty() {
        return PropertyKeyTypes.doublePropertyKey(this.getMaxAngleKey(), this.getDefaultMaxAngle());
    }
}
