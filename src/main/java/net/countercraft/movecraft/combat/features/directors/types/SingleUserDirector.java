package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.SingleUserDirectorRuntimeData;

import java.util.Map;

public abstract class SingleUserDirector extends AbstractDirector<SingleUserDirectorRuntimeData> {

    public SingleUserDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    public SingleUserDirectorRuntimeData createRuntimeData() {
        return new SingleUserDirectorRuntimeData();
    }

}
