package net.countercraft.movecraft.combat.utils;

import net.countercraft.movecraft.combat.features.directors.Directors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DirectorUtils {
    private static int distanceToRender(@NotNull Location location) {
        int chunkDisplacementX, chunkDisplacementZ;
        int renderDistance = Bukkit.getServer().getViewDistance() << 4;

        int chunkX = location.getChunk().getX() << 4; // minimum x and z positions in the chunk
        int chunkZ = location.getChunk().getZ() << 4;

        if (location.getDirection().getX() > 0) // get the number that must be added to the coordinate to move it
            chunkDisplacementX = 15 - location.getBlockX() + chunkX; // to the edge of the chunk
        else
            chunkDisplacementX = chunkX - location.getBlockX();

        if (location.getDirection().getZ() > 0)
            chunkDisplacementZ = 15 - location.getBlockZ() + chunkZ;
        else
            chunkDisplacementZ = chunkZ - location.getBlockZ();

        // add a quarter rotation to make the maths easier
        // now theta = 0 is in the positive X direction
        double theta = Math.toRadians((location.getYaw() + 90F) % 360F);

        // We then take the sine of the angle times the X magnitude or the sine of the angle times the Z magnitude,
        // whichever is smaller to establish the distance the ray travels before bumping into either edge of render.
        double xDistance = (renderDistance + Math.abs(chunkDisplacementX)) / Math.cos(theta);
        double yDistance = (renderDistance + Math.abs(chunkDisplacementZ)) / Math.sin(theta);

        double pitchRadians = Math.toRadians(location.getPitch());
        double horizontalDistance = Math.min(Math.abs(xDistance), Math.abs(yDistance)) / Math.cos(pitchRadians);
        double verticalDistance = location.getBlockY() / Math.sin(pitchRadians); // and now vertical distance

        double finalDistance = Math.min(Math.abs(horizontalDistance), Math.abs(verticalDistance));
        return (int) finalDistance; // casting to an int will floor the result, giving us a bit of safety.
    }

    public static Vector limitVectorToMaxAngle(final Vector targetVector, final Vector originalVector, final double maxAngleInRadian) {
        // Special case for when the aiming direction is to the opposite side, in that case, return the input vector
        // The originalVector is the normal of a plane. If out targetVector points to behind that plane (contrary to the normal), we are facing away from it
        final double dotProduct = targetVector.clone().normalize().dot(originalVector.clone().normalize());
        if (dotProduct < 0) {
            // We are facing away from the plane, we return what was put in
            return originalVector.clone();
        }
        // Limit the vector to a certain angle
        final double maxAngle = Math.cos(maxAngleInRadian);
        // we need to limit
        if (dotProduct < maxAngle) {
            // Step 1: Normalize the target vector (the direction we are aiming at) and the original vector => Already the case
            // Step 2: Create perpendicular vector in the same plane as both vectors
            final Vector perpendicularVector = targetVector.clone().subtract(originalVector.clone().multiply(dotProduct)).normalize();
            // Step 3: Obtain the correct, limited vector
            return originalVector.clone().add(perpendicularVector.multiply(Math.tan(maxAngleInRadian))).normalize();
        } else {
            // All good, we can use it as we wanted to
            return targetVector.clone();
        }
    }

    @Nullable
    public static Block getDirectorBlock(@NotNull Player player, int range) {
        Iterator<Block> itr = new BlockIterator(player, Math.min(range, distanceToRender(player.getLocation())));
        while (itr.hasNext()) {
            Block block = itr.next();
            Material material = block.getType();
            if (Directors.Transparent == null) {
                if (!material.equals(Material.AIR))
                    return block;
            }
            else {
                if (!Directors.Transparent.contains(material))
                    return block;
            }
        }
        return null;
    }
}