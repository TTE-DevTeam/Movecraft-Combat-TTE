package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Optional;

public class TNTDirectorDataAccess {

    static final NamespacedKey KEY_WAS_DIRECTED = new NamespacedKey(MovecraftCombat.getInstance(), "directors_was_directed");
    static final NamespacedKey KEY_INITIAL_DIRECTION_TICK = new NamespacedKey(MovecraftCombat.getInstance(), "directors_direction_tick");
    static final NamespacedKey KEY_VELOCITY_PRE_DIRECT = new NamespacedKey(MovecraftCombat.getInstance(), "directors_velocity_before_direction");

    public static boolean wasAlreadyDirected(final TNTPrimed tntPrimed) {
        return tntPrimed.getPersistentDataContainer().getOrDefault(KEY_WAS_DIRECTED, PersistentDataType.BOOLEAN, false);
    }

    public static boolean wasDirectedInSameTick(final TNTPrimed tntPrimed) {
        Optional<Long> initialTick = getTickOfDirection(tntPrimed);
        if (initialTick.isEmpty()) {
            return false;
        }
        return initialTick.get() == tntPrimed.getTicksLived();
    }

    public static Optional<Long> getTickOfDirection(final TNTPrimed tntPrimed) {
        return Optional.ofNullable(tntPrimed.getPersistentDataContainer().getOrDefault(KEY_INITIAL_DIRECTION_TICK, PersistentDataType.LONG, null));
    }

    public static Optional<Vector> getPreDirectVelocity(final TNTPrimed tntPrimed) {
        return Optional.ofNullable(tntPrimed.getPersistentDataContainer().getOrDefault(KEY_VELOCITY_PRE_DIRECT, VECTOR_PERSISTENT_DATA_TYPE, null));
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
