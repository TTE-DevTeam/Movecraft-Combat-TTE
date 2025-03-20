package net.countercraft.movecraft.combat.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class DispenserDispenseEntityEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;

    public DispenserDispenseEntityEvent(@NotNull Block theBlock, @NotNull Entity theEntity) {
        super(theBlock);
        this.entity = theEntity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
