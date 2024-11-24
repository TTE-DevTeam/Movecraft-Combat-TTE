package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.AbstractDirector;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractNamedDirector extends AbstractDirector {

    protected final Map<String, UUID> DIRECTORS = new HashMap<>();
    protected final Map<UUID, Vector> DIRECTOR_DIRECTIONS = new HashMap<>();
}
