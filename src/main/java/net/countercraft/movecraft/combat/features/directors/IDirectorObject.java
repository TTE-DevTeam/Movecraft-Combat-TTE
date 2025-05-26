package net.countercraft.movecraft.combat.features.directors;

import org.bukkit.util.Vector;

public interface IDirectorObject {

    Vector getDirectionVector(int convergenceDistance, Vector projectileLocation);

}
