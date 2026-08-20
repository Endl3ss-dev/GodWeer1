package org.privates.client.managers.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.tileentity.TileEntity;
import org.privates.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class RenderTileEntityEvent extends CancellableEvent {
    private TileEntity tileEntity;

    public static class Pre extends RenderTileEntityEvent {
        public Pre(TileEntity tileEntity) {
            super(tileEntity);
        }
    }

    public static class Post extends RenderTileEntityEvent {
        public Post(TileEntity tileEntity) {
            super(tileEntity);
        }
    }
}