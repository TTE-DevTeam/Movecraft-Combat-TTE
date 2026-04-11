package net.countercraft.movecraft.combat.features.directors;

import net.kyori.adventure.text.Component;
import org.bukkit.util.Vector;

public interface IDirectorObject {

    /*
     * Retrieves the current direction to aim at. If the stored direction is returned or not is up to the IDirectorObject implementation
     * Return null if this director does not aim at anything
     */
    Vector getDirectionVector(int convergenceDistance, Vector projectileLocation);

    void saveDirection(Vector direction);
    void resetSavedDirection();

    void sendMessage(Component message);

    default void onRemoved() {
        // Do nothing by default
    }
}
