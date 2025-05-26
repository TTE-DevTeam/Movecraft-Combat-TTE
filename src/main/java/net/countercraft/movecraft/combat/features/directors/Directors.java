package net.countercraft.movecraft.combat.features.directors;

import com.google.common.collect.HashBiMap;
import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.features.directors.types.AbstractDirector;
import net.countercraft.movecraft.combat.features.directors.types.sign.AbstractDirectorSign;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.datatag.CraftDataTagKey;
import net.countercraft.movecraft.craft.datatag.CraftDataTagRegistry;
import net.countercraft.movecraft.sign.AbstractCraftSign;
import net.countercraft.movecraft.sign.MovecraftSignRegistry;
import net.countercraft.movecraft.util.Tags;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Directors extends BukkitRunnable {
    private static final Set<Directors> instances = new HashSet<>();
    public static Material DirectorTool = null;
    public static Set<Material> Transparent = null;
    private final HashBiMap<PlayerCraft, Player> directors = HashBiMap.create();

    public static List<AbstractDirector> CONFIGURED_DIRECTORS = List.of();
    public static Map<EntityType, List<AbstractDirector>> DIRECTORS_PER_TYPE = new WeakHashMap<>();

    public static CraftDataTagKey<CraftDirectorData> DATA_TAG_KEY_DIRECTOR_DATA = CraftDataTagRegistry.INSTANCE.registerTagKey(new NamespacedKey(MovecraftCombat.getInstance(), "director-data"), CraftDirectorData::new);


    public Directors() {
        instances.add(this);
    }

    public static void load(@NotNull FileConfiguration config) {
        Object tool = config.get("DirectorTool");
        Material directorTool = null;
        if (tool instanceof String)
            directorTool = Material.getMaterial((String) tool);
        if (directorTool == null)
            MovecraftCombat.getInstance().getLogger().severe("Failed to load director tool " + ((tool == null) ? "null" : tool.toString()));
        else
            DirectorTool = directorTool;

        if (!config.contains("TransparentBlocks")) {
            Transparent = new HashSet<>();
            return;
        }
        var transparent = config.getList("TransparentBlocks");
        if (transparent == null)
            throw new IllegalStateException();

        Transparent = new HashSet<>();
        for (Object o : transparent) {
            if (o instanceof String) {
                var tagged = Tags.parseMaterials((String) o);
                Transparent.addAll(tagged);
            }
            else {
                MovecraftCombat.getInstance().getLogger().severe("Failed to load transparent " + o.toString());
            }
        }

        try {
            List<? extends AbstractDirector> directorList = (List<? extends AbstractDirector>) config.getList("Directors", List.of());
            directorList.sort(Comparator.naturalOrder());
            for (AbstractDirector adTmp : directorList) {
                CONFIGURED_DIRECTORS.add(adTmp);

                for (EntityType type : adTmp.getEntityTypes()) {
                    DIRECTORS_PER_TYPE.computeIfAbsent(type, k -> new ArrayList<>()).add(adTmp);
                }

                adTmp.registerCraftTypeProperties();
                AbstractDirectorSign directorSign = adTmp.createDirectorSignHandler();
                if (directorSign != null && directorSign.getSignIdent() != null && !directorSign.getSignIdent().isBlank()) {
                    MovecraftSignRegistry.INSTANCE.register(directorSign.getSignIdent(), directorSign, true);
                }
            }
            for (List<AbstractDirector> list : DIRECTORS_PER_TYPE.values()) {
                list.sort(Comparator.naturalOrder());
            }
        } catch(Exception ex) {
            MovecraftCombat.getInstance().getLogger().severe("Unable to load Directors config section!");
        }
    }

    @Override
    public void run() {

    }


    public void addDirector(@NotNull PlayerCraft craft, @NotNull Player player) {
        if (directors.containsValue(player))
            directors.inverse().remove(player);

        directors.put(craft, player);
    }

    public boolean isDirector(@NotNull Player player) {
        return directors.containsValue(player);
    }

    public boolean hasDirector(@NotNull PlayerCraft craft) {
        if (!directors.containsKey(craft))
            return false;

        Player director = directors.get(craft);
        return director != null && director.isOnline();
    }

    public void removeDirector(@NotNull Player player) {
        directors.inverse().remove(player);
    }

    public static void clearDirector(@NotNull Player player) {
        for (var instance : instances)
            instance.removeDirector(player);
    }

    public static boolean isAnyDirector(@NotNull Player player) {
        for (var instance : instances) {
            if (instance.isDirector(player))
                return true;
        }
        return false;
    }

    @Nullable
    public Player getDirector(@NotNull PlayerCraft craft) {
        Player director = directors.get(craft);
        if (director == null || !director.isOnline())
            return null;

        return director;
    }
}
