package net.countercraft.movecraft.combat.features.directors.types.sign;

import net.countercraft.movecraft.combat.features.directors.CraftDirectorData;
import net.countercraft.movecraft.combat.features.directors.Directors;
import net.countercraft.movecraft.combat.features.directors.LivingEntityDirector;
import net.countercraft.movecraft.combat.features.directors.types.AbstractDirector;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.sign.SignListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.block.HangingSign;
import org.bukkit.entity.Player;

public class SimpleDirectorSign extends AbstractDirectorSign {
    public SimpleDirectorSign(String signIdent, AbstractDirector associatedDirector) {
        super(signIdent, associatedDirector);
    }

    @Override
    protected boolean onBeforeToggle(Craft craft, SignListener.SignWrapper signWrapper, Player player, boolean willBeOn) {
        CraftDirectorData directorData = craft.getDataTag(Directors.DATA_TAG_KEY_DIRECTOR_DATA);
        if (directorData == null) {
            return false;
        }

        // willBeOn => will have a director
        if (willBeOn) {
            if (!directorData.addDirector(this.associatedDirector, new LivingEntityDirector(player), collectParameters(signWrapper))) {
                return false;
            }
            // Set the director's name
            // TODO: Properly calculate the characters! Technically it depends on the characters size if they fit onto the sign or not!
            int characterLimit = 15;
            if (signWrapper.block() instanceof HangingSign) {
                characterLimit = 10;
            }
            signWrapper.line(INDEX_DIRECTOR_NAME, Component.text(player.getDisplayName().substring(0, characterLimit - 1), Style.style(COLOR_DIRECTOR_NAME)));
        } else {
            if (!directorData.removeDirector(new LivingEntityDirector(player), collectParameters(signWrapper))) {
                return false;
            }
            signWrapper.line(INDEX_DIRECTOR_NAME, Component.text(""));
        }
        return true;
    }

    @Override
    protected void onAfterToggle(Craft craft, SignListener.SignWrapper signWrapper, Player player, boolean b) {
        // This can be called multiple times, thats why we dont use it
    }

    @Override
    protected void onCraftIsBusy(Player player, Craft craft) {
        // Ignored
    }

    @Override
    protected void onCraftNotFound(Player player, SignListener.SignWrapper signWrapper) {
        // Can be ignored here!
    }
}
