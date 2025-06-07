package net.countercraft.movecraft.combat.utils;

import org.bukkit.NamespacedKey;

import java.util.Map;

public class ConfigHelper {

    public static boolean readBoolean(Map<String, Object> args, String key, boolean defaultValue) {
        Object obj = args.getOrDefault(key, defaultValue);
        boolean result = defaultValue;
        if (obj != null && (obj instanceof Boolean)) {
            result = ((Boolean) obj).booleanValue();
        }
        return result;
    }

    public static NamespacedKey readNamespacedKey(Map<String, Object> args, String key, NamespacedKey defaultValue) {
        Object obj = args.getOrDefault(key, defaultValue);
        NamespacedKey result = defaultValue;
        if (obj != null && (obj instanceof String)) {
            result = NamespacedKey.fromString((String) obj);
        }
        return result;
    }

    public static String namespaceToCraftKey(final NamespacedKey key) {
        String result = "";
        boolean nextIsUpper = false;
        for (int i = 0; i < key.getKey().length(); i++) {
            char c = key.getKey().charAt(i);

            if (c == '-' || c == '_') {
                nextIsUpper = true;
                continue;
            }

            if (nextIsUpper) {
                c = Character.toUpperCase(c);
                nextIsUpper = false;
            } else {
                c = Character.toLowerCase(c);
            }
            result = result + c;
        }
        return result;
    }

}
