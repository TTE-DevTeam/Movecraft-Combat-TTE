package net.countercraft.movecraft.combat.features.directors;

import net.countercraft.movecraft.combat.event.DirectorRegistrationEvent;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DirectorRegister {

    private static final List<AbstractDirector.DirectorConstructor> registeredDirectors;

    public static void applyDirectorInstances(Consumer<AbstractDirector> addFunction, BiConsumer<String, AbstractDirector> mappingFunction) {
        for (AbstractDirector.DirectorConstructor constructor : registeredDirectors) {
            AbstractDirector instance = constructor.create();
            addFunction.accept(instance);
            mappingFunction.accept(constructor.name(), instance);
        }
    }

    static {
        registeredDirectors = new ArrayList<>();

        DirectorRegistrationEvent event = new DirectorRegistrationEvent(registeredDirectors);
        Bukkit.getServer().getPluginManager().callEvent(event);

        registeredDirectors.sort(Comparator.comparingInt(AbstractDirector.DirectorConstructor::priority));
    }

}
