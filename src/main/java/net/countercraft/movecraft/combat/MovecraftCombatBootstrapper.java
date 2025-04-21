package net.countercraft.movecraft.combat;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.event.block.BlockDispenseEvent;

public class MovecraftCombatBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {

            // Ugly hack but it works
            protected ProjectileItem.DispenseConfig dispenseConfig = ((ProjectileItem)Items.FIRE_CHARGE).createDispenseConfig();

            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                Level level = blockSource.level();
                Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                Block bukkitBlock = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(item.copyWithCount(1));
                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), CraftVector.toBukkit(blockPos));
                if (!DispenserBlock.eventFired) {
                    level.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return item;
                } else {
                    if (!event.getItem().equals(craftItem)) {
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        DispenseItemBehavior dispenseBehavior = DispenserBlock.getDispenseBehavior(blockSource, eventStack);
                        if (dispenseBehavior != DispenseItemBehavior.NOOP && dispenseBehavior != this) {
                            dispenseBehavior.dispense(blockSource, eventStack);
                            return item;
                        }
                    }

                    if (level.isEmptyBlock(blockPos) && WitherSkullBlock.canSpawnMob(level, blockPos, item)) {
                        level.setBlock(blockPos, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, RotationSegment.convertToSegment(direction)), 3);
                        level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        if (blockEntity instanceof SkullBlockEntity) {
                            WitherSkullBlock.checkSpawn(level, blockPos, (SkullBlockEntity)blockEntity);
                        }

                        item.shrink(1);
                        this.setSuccess(true);
                    } if (EquipmentDispenseItemBehavior.dispenseEquipment(blockSource, item, this)) {
                        this.setSuccess(true);
                    } else {
                        // Try to shoot it!!
                        ServerLevel serverLevel = blockSource.level();
                        Position dispensePosition = this.dispenseConfig.positionFunction().getDispensePosition(blockSource, direction);
                        ItemStack itemstack1 = item.copyWithCount(1);

                        if (!itemstack1.isEmpty()) {
                            WitherSkull witherSkull = new WitherSkull(EntityType.WITHER_SKULL, serverLevel);
                            // Set position
                            witherSkull.setPos(dispensePosition.x(), dispensePosition.y(), dispensePosition.z());
                            // Set direction / rotation
                            witherSkull.setRot(direction.toYRot(), 0.0F);

                            Projectile iprojectile = Projectile.spawnProjectileUsingShoot(witherSkull, serverLevel, itemstack1, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), this.dispenseConfig.power(), this.dispenseConfig.uncertainty());
                            iprojectile.projectileSource = new CraftBlockProjectileSource(blockSource.blockEntity());
                        }

                        item.shrink(1);
                    }

                    return item;
                }
            }
        });
    }

}
