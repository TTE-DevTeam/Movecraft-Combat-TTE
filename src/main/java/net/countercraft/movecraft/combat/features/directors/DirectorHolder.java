package net.countercraft.movecraft.combat.features.directors;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DirectorHolder {

    private final int priority;
    private final @NotNull Supplier<IDirector> directorInstance;

    public DirectorHolder(int priority, final @NotNull Supplier<IDirector> director) {
        this.priority = priority;
        this.directorInstance = director;
    }

    public Supplier<IDirector> getDirectorInstance() {
        return this.directorInstance;
    }

    public int getPriority() {
        return this.priority;
    }

}
