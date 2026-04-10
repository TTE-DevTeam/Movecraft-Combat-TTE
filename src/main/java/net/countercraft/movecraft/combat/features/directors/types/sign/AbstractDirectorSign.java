package net.countercraft.movecraft.combat.features.directors.types.sign;

import net.countercraft.movecraft.combat.features.directors.types.AbstractDirector;
import net.countercraft.movecraft.combat.features.directors.types.AbstractMultiUserDirector;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.sign.AbstractToggleSign;
import net.countercraft.movecraft.sign.SignListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDirectorSign extends AbstractToggleSign {

    protected final AbstractDirector associatedDirector;

    public AbstractDirectorSign(final String signIdent, final AbstractDirector associatedDirector) {
        super(true, signIdent, "", "");
        this.associatedDirector = associatedDirector;
    }

    public String getSignIdent() {
        return this.ident;
    }

    public static final byte INDEX_DIRECTOR_NAME = 1;
    public static final byte INDEX_ARGUMENT_ONE = 2;
    public static final byte INDEX_ARGUMENT_TWO = 3;
    public static final byte[] ARGUMENT_INDIZES = {INDEX_ARGUMENT_ONE, INDEX_ARGUMENT_TWO};

    protected String[] collectParameters(SignListener.SignWrapper signWrapper) {
        List<String> resultTmp = new ArrayList<>();
        for (byte i : ARGUMENT_INDIZES) {
            if (!signWrapper.getRaw(i).isBlank()) {
                resultTmp.add(signWrapper.getRaw(i));
            }
        }
        return resultTmp.toArray(new String[resultTmp.size()]);
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

    @Override
    protected boolean shouldShareSameToggleState(SignListener.SignWrapper sign, SignListener.SignWrapper other, Craft craft) {
        boolean result = super.shouldShareSameToggleState(sign, other, craft);
        // For nodal or multiuser directors, compare the node ident too!
        if (result && this.associatedDirector instanceof AbstractMultiUserDirector) {
            result = sign.getRaw(1).equalsIgnoreCase(other.getRaw(1));
        }
        return result;
    }

    protected static final TextColor COLOR_DIRECTOR_NAME = TextColor.color(0, 200, 255);
    protected static final TextColor COLOR_DIRECTOR_IS_FREE = TextColor.color(0, 255, 0);
    protected static final TextColor COLOR_DIRECTOR_IS_TAKEN = TextColor.color(255, 0, 0);

    // TODO: Implement
    @Override
    protected Component buildHeaderOff() {
        // Director is "free" => green
        return Component.text(this.getSignIdent(), Style.style(COLOR_DIRECTOR_IS_FREE));
    }

    @Override
    protected Component buildHeaderOn() {
        // Director is "taken" => red
        return Component.text(this.getSignIdent(), Style.style(COLOR_DIRECTOR_IS_TAKEN));
    }

    @Override
    public void onCraftStatusUpdate(Craft craft, SignListener.SignWrapper sign) {
        super.onCraftStatusUpdate(craft, sign);

        // Update the director state depending on the director's data
    }

    @Override
    protected boolean canBeResettedBy(AbstractToggleSign caller) {
        if (caller instanceof AbstractDirectorSign abstractDirectorSign) {
            return abstractDirectorSign.associatedDirector == this.associatedDirector;
        } else {
            return super.canBeResettedBy(caller);
        }
    }

    @Override
    protected boolean isOnOrOff(SignListener.SignWrapper sign) {
        Component header = sign.line(0);
        // Color green => OFF ("FREE")
        // Color red => ON ("TAKEN")
        return header.color().equals(COLOR_DIRECTOR_IS_TAKEN);
    }

    @Override
    protected boolean isSignValid(Action clickType, SignListener.SignWrapper sign, Player player) {
        if (PlainTextComponentSerializer.plainText().serialize(sign.line(0)).isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
