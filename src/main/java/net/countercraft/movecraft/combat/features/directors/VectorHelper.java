package net.countercraft.movecraft.combat.features.directors;

import org.bukkit.Axis;
import org.bukkit.util.Vector;

public class VectorHelper {

    public static float getAngleBetween(Vector a, Vector b, Axis axis) {
        double x1 = 0, y1 = 0;
        double x2 = 0, y2 = 0;
        switch(axis) {
            case X:
                x1 = a.getY();
                y1 = a.getZ();
                x2 = b.getY();
                y2 = b.getZ();
                break;
            case Y:
                x1 = a.getX();
                y1 = a.getZ();
                x2 = b.getX();
                y2 = b.getZ();
                break;
            case Z:
                x1 = a.getX();
                y1 = a.getY();
                x2 = b.getX();
                y2 = b.getY();
                break;
        }
        final double lengthA = Math.sqrt((x1 * x1) + (y1 * y1));
        final double lengthB = Math.sqrt((x2 * x2) + (y2 * y2));

        final double aDotB = (x1 * x2) + (y1 * y2);

        return (float)Math.toDegrees(Math.acos(aDotB / (lengthA * lengthB)));
    }
}
