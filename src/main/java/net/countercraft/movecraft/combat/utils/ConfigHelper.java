package net.countercraft.movecraft.combat.utils;

import java.util.Map;

public class ConfigHelper {

    public static boolean readBoolean(Map<String, Object> args, String key, boolean defaultValue) {
        Object objectRequiresCraft = args.getOrDefault(key, defaultValue);
        boolean result = defaultValue;
        if (objectRequiresCraft != null && (objectRequiresCraft instanceof Boolean)) {
            result = ((Boolean) objectRequiresCraft).booleanValue();
        }
        return result;
    }

}
