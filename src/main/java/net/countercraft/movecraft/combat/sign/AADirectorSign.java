package net.countercraft.movecraft.combat.sign;

import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.combat.directors.AADirectorManager;
import net.countercraft.movecraft.combat.localisation.I18nSupport;
import net.countercraft.movecraft.craft.PlayerCraft;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.combat.config.Config;
import org.jetbrains.annotations.Nullable;

import static net.countercraft.movecraft.util.ChatUtils.ERROR_PREFIX;


public class AADirectorSign implements Listener {
    private static final String HEADER = "AA Director";

    @EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if(!isSign(block)) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(HEADER)) {
            return;
        }

        Player player = event.getPlayer();
        Craft foundCraft = null;
        for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(block.getWorld())) {
            if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(block.getLocation())) &&
                    CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                foundCraft = tcraft;
                break;
            }
        }
        if (foundCraft == null) {
            player.sendMessage(ERROR_PREFIX + " " + I18nSupport.getInternationalisedString("Sign - Must Be Part Of Craft"));
            return;
        }

        if (!foundCraft.getType().getBoolProperty(AADirectorManager.ALLOW_AA_DIRECTOR_SIGN) || !(foundCraft instanceof PlayerCraft)) {
            player.sendMessage(ERROR_PREFIX + " " + I18nSupport.getInternationalisedString("AADirector - Not Allowed On Craft"));
            return;
        }

        AADirectorManager aa = MovecraftCombat.getInstance().getAADirectors();
        if(event.getAction() == Action.LEFT_CLICK_BLOCK && aa.isDirector(player)){
            aa.removeDirector(event.getPlayer());
            player.sendMessage(I18nSupport.getInternationalisedString("AADirector - No Longer Directing"));
            return;
        }

        aa.addDirector((PlayerCraft) foundCraft, event.getPlayer());
        player.sendMessage(I18nSupport.getInternationalisedString("AADirector - Directing"));
        if (MovecraftCombat.getInstance().getCannonDirectors().isDirector(player)) {
            MovecraftCombat.getInstance().getCannonDirectors().removeDirector(player);
        }
    }

    private boolean isSign(@Nullable Block block){
        if (block == null)
            return false;
        final Material type = block.getType();

        return type.name().equals("SIGN_POST") ||
                type.name().equals("SIGN") ||
                type.name().equals("WALL_SIGN") ||
                type.name().endsWith("_SIGN") ||
                type.name().endsWith("_WALL_SIGN");
    }
}