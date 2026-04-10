package net.countercraft.movecraft.combat.features.directors;

import net.kyori.adventure.text.Component;
import org.bukkit.util.Vector;

public interface IDirectorObject {

    Vector getDirectionVector(int convergenceDistance, Vector projectileLocation);

    void saveDirection(Vector direction);
    void resetSavedDirection();

    void sendMessage(Component message);

    default void onRemoved() {
        // Do nothing by default
    }
}
