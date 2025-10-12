package net.countercraft.movecraft.combat.features;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.util.hitboxes.MutableHitBox;
import net.countercraft.movecraft.util.hitboxes.SolidHitBox;
import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Fire;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddFiresToHitbox implements Listener {
    public static boolean AddFiresToHitbox = true;

    public static void load(@NotNull FileConfiguration config) {
        AddFiresToHitbox = config.getBoolean("AddFiresToHitbox", true);
    }


    @Nullable
    private Craft adjacentCraft(@NotNull Location location) {
        Craft craft = MathUtils.fastNearestCraftToLoc(CraftManager.getInstance().getCrafts(), location);
        if (craft == null)
            return null; //return null if no craft found

        if (MathUtils.locationInHitBox(craft.getHitBox(), location.add(1, 0, 0))
                || MathUtils.locationInHitBox(craft.getHitBox(), location.add(-1, 0, 0))
                || MathUtils.locationInHitBox(craft.getHitBox(), location.add(0, 1, 0))
                || MathUtils.locationInHitBox(craft.getHitBox(), location.add(0, -1, 0))
                || MathUtils.locationInHitBox(craft.getHitBox(), location.add(0, 0, 1))
                || MathUtils.locationInHitBox(craft.getHitBox(), location.add(0, 0, -1)))
            return craft;

        return null;
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockIgnite(@NotNull BlockIgniteEvent e) {
        Craft craft = adjacentCraft(e.getBlock().getLocation());
        if (craft == null || craft.getHitBox().isEmpty())
            return;
        if (!(craft.getHitBox() instanceof MutableHitBox))
            return;

        MutableHitBox hitbox = (MutableHitBox) craft.getHitBox();
        hitbox.add(MathUtils.bukkit2MovecraftLoc(e.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent e) {
        // Allow punching fire out on crafts
        if (e.getBlock().getType() != Material.FIRE)
            return;
        Craft craft = adjacentCraft(e.getBlock().getLocation());
        if (craft == null || craft.getHitBox().isEmpty())
            return;
        if (!(craft.getHitBox() instanceof MutableHitBox))
            return;

        MutableHitBox hitbox = (MutableHitBox) craft.getHitBox();
        hitbox.remove(MathUtils.bukkit2MovecraftLoc(e.getBlock().getLocation()));
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getExplosionResult() == ExplosionResult.KEEP) {
            return;
        }
        addFiresFromExplosion(new ArrayList<>(event.blockList()), event.getEntity().getLocation(), event.getYield());
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        if (event.getExplosionResult() == ExplosionResult.KEEP) {
            return;
        }
        addFiresFromExplosion(new ArrayList<>(event.blockList()), event.getBlock().getLocation(), event.getYield());
    }

    static void addFiresFromExplosion(@NotNull List<Block> blocks, @NotNull Location location, float radius) {
        blocks.removeIf(block -> {
           if (!(block.getBlockData() instanceof Fire)) {
               return true;
           }
           return false;
        });
        // Make the radius a good bit larger
        final int rad = (int) Math.ceil(radius + 2);
        MovecraftLocation min = new MovecraftLocation(location.getBlockX() - rad, location.getBlockY() - rad, location.getBlockZ() - rad);
        MovecraftLocation max = new MovecraftLocation(location.getBlockX() + rad, location.getBlockY() + rad, location.getBlockZ() + rad);
        HitBox hitBox = new SolidHitBox(min, max);
        for (Craft craft : CraftManager.getInstance().getCraftsInWorld(location.getWorld())) {
            if (craft.getHitBox().intersection(hitBox).isEmpty()) {
                continue;
            }
            if (!(craft.getHitBox() instanceof MutableHitBox))
                continue;

            MutableHitBox hitbox = (MutableHitBox) craft.getHitBox();
            for (Block block : blocks) {
                hitbox.add(MathUtils.bukkit2MovecraftLoc(block.getLocation()));
            }
        }
    }
}
