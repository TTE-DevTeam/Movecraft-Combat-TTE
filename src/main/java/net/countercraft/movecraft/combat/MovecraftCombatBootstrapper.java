package net.countercraft.movecraft.combat;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.countercraft.movecraft.combat.event.DispenserDispenseEntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class MovecraftCombatBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                Level level = blockSource.level();
                BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                // CraftBukkit start
                ItemStack singleItemStack = item.copyWithCount(1); // Paper - shrink at end and single item in event
                org.bukkit.block.Block block = org.bukkit.craftbukkit.block.CraftBlock.at(level, blockSource.pos());
                org.bukkit.craftbukkit.inventory.CraftItemStack craftItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(singleItemStack);

                org.bukkit.event.block.BlockDispenseEvent event = new org.bukkit.event.block.BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) blockPos.getX() + 0.5D, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5D));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    // item.grow(1); // Paper - shrink below
                    return item;
                }

                boolean shrink = true; // Paper
                if (!event.getItem().equals(craftItem)) {
                    shrink = false; // Paper - shrink below
                    // Chain to handler for new item
                    ItemStack eventStack = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior dispenseBehavior = DispenserBlock.getDispenseBehavior(blockSource, eventStack); // Paper - Fix NPE with equippable and items without behavior
                    if (dispenseBehavior != DispenseItemBehavior.NOOP && dispenseBehavior != this) {
                        dispenseBehavior.dispense(blockSource, eventStack);
                        return item;
                    }
                }

                PrimedTnt primedTnt = new PrimedTnt(level, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), null);

                DispenserDispenseEntityEvent ddee = new DispenserDispenseEntityEvent(block, primedTnt.getBukkitEntity());
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(ddee);
                }

                // CraftBukkit end
                level.addFreshEntity(primedTnt);
                level.playSound(null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                if (shrink) item.shrink(1); // Paper - actually handle here
                return item;
            }
        });

        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack item) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                EntityType<?> type = ((SpawnEggItem)item.getItem()).getType(blockSource.level().registryAccess(), item);

                // CraftBukkit start
                ServerLevel serverLevel = blockSource.level();
                ItemStack singleItemStack = item.copyWithCount(1); // Paper - shrink below and single item in event
                org.bukkit.block.Block block = org.bukkit.craftbukkit.block.CraftBlock.at(serverLevel, blockSource.pos());
                org.bukkit.craftbukkit.inventory.CraftItemStack craftItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(singleItemStack);

                org.bukkit.event.block.BlockDispenseEvent event = new org.bukkit.event.block.BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!DispenserBlock.eventFired) {
                    serverLevel.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    // item.grow(1); // Paper - shrink below
                    return item;
                }

                boolean shrink = true; // Paper
                if (!event.getItem().equals(craftItem)) {
                    shrink = false; // Paper - shrink below
                    // Chain to handler for new item
                    ItemStack eventStack = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem());
                    DispenseItemBehavior dispenseBehavior = DispenserBlock.getDispenseBehavior(blockSource, eventStack); // Paper - Fix NPE with equippable and items without behavior
                    if (dispenseBehavior != DispenseItemBehavior.NOOP && dispenseBehavior != this) {
                        dispenseBehavior.dispense(blockSource, eventStack);
                        return item;
                    }
                    // Paper start - track changed items in the dispense event
                    singleItemStack = org.bukkit.craftbukkit.inventory.CraftItemStack.unwrap(event.getItem()); // unwrap is safe because the stack won't be modified
                    type = ((SpawnEggItem) singleItemStack.getItem()).getType(serverLevel.registryAccess(), singleItemStack);
                    // Paper end - track changed item from dispense event
                }
                try {
                    Entity entity = type.spawn(
                            blockSource.level(), singleItemStack, null, blockSource.pos().relative(direction), EntitySpawnReason.DISPENSER, direction != Direction.UP, false // Paper - track changed item in dispense event
                    );
                    DispenserDispenseEntityEvent ddee = new DispenserDispenseEntityEvent(block, entity.getBukkitEntity());
                    if (!DispenserBlock.eventFired) {
                        serverLevel.getCraftServer().getPluginManager().callEvent(ddee);
                    }
                } catch (Exception var6) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), var6);
                    return ItemStack.EMPTY;
                }

                if (shrink) item.shrink(1); // Paper - actually handle here
                // CraftBukkit end
                blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
                return item;
            }
        };

        for (SpawnEggItem spawnEggItem : SpawnEggItem.eggs()) {
            DispenserBlock.registerBehavior(spawnEggItem, defaultDispenseItemBehavior);
        }
    }

}
