package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.MultiUserDirectorRuntimeData;

import java.util.Map;

public abstract class AbstractMultiUserDirector<T extends MultiUserDirectorRuntimeData> extends AbstractDirector<T> {

    public AbstractMultiUserDirector(Map rawData) {
        super(rawData);
    }
}
