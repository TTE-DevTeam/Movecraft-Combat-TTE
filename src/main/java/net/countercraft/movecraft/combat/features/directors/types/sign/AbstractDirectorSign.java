package net.countercraft.movecraft.combat.features.directors.types.sign;

import net.countercraft.movecraft.combat.features.directors.types.AbstractDirector;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.sign.AbstractCraftSign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDirectorSign extends AbstractCraftSign {

    private final String signIdent;
    protected final AbstractDirector associatedDirector;

    public AbstractDirectorSign(final String signIdent, final AbstractDirector associatedDirector) {
        super(false);
        this.signIdent = signIdent;
        this.associatedDirector = associatedDirector;
    }

    public String getSignIdent() {
        return this.signIdent;
    }

    @Override
    protected boolean canPlayerUseSignOn(Player player, @Nullable Craft craft) {
        if (!super.canPlayerUseSignOn(player, craft)) {
            return false;
        }
        if (craft != null) {
            return this.associatedDirector.allowedOnCraft(craft);
        }
        return false;
    }
}
