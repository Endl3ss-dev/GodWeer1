/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.hud.PlayerListHud
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PlayerListHud.class})
public interface PlayerListHudAccessor {
    @Accessor(value="header")
    public Text getHeader();

    @Accessor(value="footer")
    public Text getFooter();

    @Accessor(value="header")
    public void setHeaderValue(Text var1);

    @Accessor(value="footer")
    public void setFooterValue(Text var1);

    @Accessor(value="visible")
    public boolean isVisible();

    @Accessor(value="visible")
    public void setVisibleValue(boolean var1);
}

