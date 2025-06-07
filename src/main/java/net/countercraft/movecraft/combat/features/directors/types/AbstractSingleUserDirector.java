package net.countercraft.movecraft.combat.features.directors.types;

import net.countercraft.movecraft.combat.features.directors.types.data.SingleUserDirectorRuntimeData;

import java.util.Map;

public abstract class AbstractSingleUserDirector extends AbstractDirector<SingleUserDirectorRuntimeData> {

    public AbstractSingleUserDirector(Map<String, Object> rawData) {
        super(rawData);
    }

    @Override
    public SingleUserDirectorRuntimeData createRuntimeData() {
        return new SingleUserDirectorRuntimeData();
    }

}
