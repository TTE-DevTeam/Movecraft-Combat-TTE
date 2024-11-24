package net.countercraft.movecraft.combat.event;

import net.countercraft.movecraft.combat.features.directors.AbstractDirector;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DirectorRegistrationEvent extends Event {

    private final List<AbstractDirector.DirectorConstructor> directorRegistrar;
    private static final HandlerList handlers = new HandlerList();

    public DirectorRegistrationEvent(final List<AbstractDirector.DirectorConstructor> directorRegistrar) {
        this.directorRegistrar = directorRegistrar;
    }

    public boolean registerDirector(final @NotNull Function<String, AbstractDirector> director, final String name) {
        return this.registerDirector(director, directorRegistrar.size(), name);
    }

    public boolean registerDirector(final @NotNull Function<String, AbstractDirector> director, int priority, final String name) {
       this.directorRegistrar.add(new AbstractDirector.DirectorConstructor(name, priority, director));
        return true;
    }


    @Override
	public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
