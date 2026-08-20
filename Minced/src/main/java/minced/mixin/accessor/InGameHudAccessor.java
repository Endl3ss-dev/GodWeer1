/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.hud.InGameHud
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={InGameHud.class})
public interface InGameHudAccessor {
    @Accessor(value="overlayMessage")
    public Text getOverlayMessage();

    @Accessor(value="overlayMessage")
    public void setOverlayMessageValue(Text var1);

    @Accessor(value="overlayRemaining")
    public int getOverlayRemaining();

    @Accessor(value="overlayRemaining")
    public void setOverlayRemaining(int var1);

    @Accessor(value="overlayTinted")
    public boolean isOverlayTinted();

    @Accessor(value="overlayTinted")
    public void setOverlayTinted(boolean var1);

    @Accessor(value="title")
    public Text getTitleValue();

    @Accessor(value="title")
    public void setTitleValue(Text var1);

    @Accessor(value="subtitle")
    public Text getSubtitleValue();

    @Accessor(value="subtitle")
    public void setSubtitleValue(Text var1);

    @Accessor(value="titleRemainTicks")
    public int getTitleRemainTicks();

    @Accessor(value="titleRemainTicks")
    public void setTitleRemainTicks(int var1);

    @Accessor(value="titleFadeInTicks")
    public int getTitleFadeInTicks();

    @Accessor(value="titleFadeInTicks")
    public void setTitleFadeInTicks(int var1);

    @Accessor(value="titleStayTicks")
    public int getTitleStayTicks();

    @Accessor(value="titleStayTicks")
    public void setTitleStayTicks(int var1);

    @Accessor(value="titleFadeOutTicks")
    public int getTitleFadeOutTicks();

    @Accessor(value="titleFadeOutTicks")
    public void setTitleFadeOutTicks(int var1);
}



