package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.MultiUserDirectorRuntimeData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class MultiUserProjectileDirector extends AbstractMultiUserDirector<MultiUserDirectorRuntimeData> implements ProjectileDirector {

    private boolean disableGravity;

    private NamespacedKey allowedOnCraftKey;
    private boolean defaultAllowedValue = false;

    private NamespacedKey maxAngleKey;
    private double defaultMaxAngle = 45.0D;

    private ItemStack visualItemStack = null;

    public MultiUserProjectileDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    protected void deserialize(Map<String, Object> rawData) {
        this.projectileDirector_deserialize(rawData);
    }

    @Override
    public @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized) {
        return this.projectileDirector_addToSerialize(serialized);
    }

    @Override
    public MultiUserDirectorRuntimeData createRuntimeData() {
        return new MultiUserDirectorRuntimeData();
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Double>> getMaxAngleCraftTypeDoubleProperty() {
        return projectileDirector_getMaxAngleCraftTypeDoubleProperty();
    }

    @Override
    protected Triple<String, NamespacedKey, Function<CraftType, Boolean>> getAllowedOnCraftCraftTypeBooleanProperty() {
        return projectileDirector_getAllowedOnCraftCraftTypeBooleanProperty();
    }

    @Override
    protected void applyVelocity(@NotNull Craft craft, Entity entity, Vector supposedVelocity) {
        super.applyVelocity(craft, entity, supposedVelocity);

        this.projectileDirector_applyVelocity(craft, entity, supposedVelocity);
    }

    @Override
    public void setDisableGravity(boolean value) {
        this.disableGravity = value;
    }

    @Override
    public boolean getDisableGravity() {
        return this.disableGravity;
    }

    @Override
    public void setAllowedOnCraftKey(NamespacedKey value) {
        this.allowedOnCraftKey = value;
    }

    @Override
    public NamespacedKey getALlowedOnCraftKey() {
        return this.allowedOnCraftKey;
    }

    @Override
    public void setMaxAngleKey(NamespacedKey value) {
        this.maxAngleKey = value;
    }

    @Override
    public NamespacedKey getMaxAngleKey() {
        return this.maxAngleKey;
    }

    @Override
    public void setDefaultMaxAngle(double value) {
        this.defaultMaxAngle = value;
    }

    @Override
    public double getDefaultMaxAngle() {
        return this.defaultMaxAngle;
    }

    @Override
    public void setDefaultAllowedOnCraft(boolean value) {
        this.defaultAllowedValue = value;
    }

    @Override
    public boolean getDefaultAllowedOnCraft() {
        return this.defaultAllowedValue;
    }

    @Override
    public void setVisualItemStack(ItemStack value) {
        this.visualItemStack = value;
    }

    @Override
    public ItemStack getVisualItemStack() {
        return this.visualItemStack;
    }
}
