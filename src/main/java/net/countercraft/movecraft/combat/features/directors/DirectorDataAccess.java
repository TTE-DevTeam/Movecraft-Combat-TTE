package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Optional;

public class DirectorDataAccess {

    static final NamespacedKey KEY_WAS_DIRECTED = new NamespacedKey(MovecraftCombat.getInstance(), "directors_was_directed");
    static final NamespacedKey KEY_INITIAL_DIRECTION_TICK = new NamespacedKey(MovecraftCombat.getInstance(), "directors_direction_tick");
    static final NamespacedKey KEY_VELOCITY_PRE_DIRECT = new NamespacedKey(MovecraftCombat.getInstance(), "directors_velocity_before_direction");

    public static void markDirectionPoint(final Entity entity) {
        entity.getPersistentDataContainer().set(KEY_WAS_DIRECTED, PersistentDataType.BOOLEAN, true);
        entity.getPersistentDataContainer().set(KEY_INITIAL_DIRECTION_TICK, PersistentDataType.INTEGER, entity.getTicksLived());
    }

    public static boolean wasAlreadyDirected(final Entity entity) {
        return entity.getPersistentDataContainer().getOrDefault(KEY_WAS_DIRECTED, PersistentDataType.BOOLEAN, false);
    }

    public static boolean wasDirectedInSameTick(final Entity entity) {
        Optional<Integer> initialTick = getTickOfDirection(entity);
        if (initialTick.isEmpty()) {
            return false;
        }
        return initialTick.get() == entity.getTicksLived();
    }

    public static Optional<Integer> getTickOfDirection(final Entity entity) {
        return Optional.ofNullable(entity.getPersistentDataContainer().getOrDefault(KEY_INITIAL_DIRECTION_TICK, PersistentDataType.INTEGER, null));
    }

    public static void setPreDirectionVelocity(final Entity entity, final Vector vector) {
        entity.getPersistentDataContainer().set(KEY_VELOCITY_PRE_DIRECT, VECTOR_PERSISTENT_DATA_TYPE, vector);
    }

    public static Vector getPreDirectVelocity(final Entity entity) {
        Optional<Vector> opt = getOptionalPreDirectVelocity(entity);
        return opt.orElseGet(() -> new Vector(0,0,0));
    }

    public static Optional<Vector> getOptionalPreDirectVelocity(final Entity entity) {
        return Optional.ofNullable(entity.getPersistentDataContainer().getOrDefault(KEY_VELOCITY_PRE_DIRECT, VECTOR_PERSISTENT_DATA_TYPE, null));
    }

    public static final PersistentDataType<byte[], Vector> VECTOR_PERSISTENT_DATA_TYPE = new VectorPersistentDataType();

    public static class VectorPersistentDataType implements PersistentDataType<byte[], Vector> {

        @Override
        public @NotNull Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @Override
        public @NotNull Class<Vector> getComplexType() {
            return Vector.class;
        }

        @Override
        public byte @NotNull [] toPrimitive(@NotNull Vector vector, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            // A single double is 64 bit. A byte is 8 bit => double is 8 byte
            ByteBuffer buff = ByteBuffer.wrap(new byte[24]);
            buff.putDouble(vector.getX());
            buff.putDouble(vector.getY());
            buff.putDouble(vector.getZ());
            return buff.array();
        }

        @Override
        public @NotNull Vector fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            ByteBuffer buff = ByteBuffer.wrap(bytes);
            Vector result = new Vector(buff.getDouble(), buff.getDouble(), buff.getDouble());
            return result;
        }
    }


}
