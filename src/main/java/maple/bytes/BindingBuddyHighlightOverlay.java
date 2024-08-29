package maple.bytes;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.time.Instant;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;


public class BindingBuddyHighlightOverlay extends Overlay {

    public final BindingBuddyPlugin plugin;
    public final Client client;
    public final BindingBuddyConfig config;

    private long lastFlashTime = 0;
    private boolean isHighlightVisible = true;


    @Inject
    public BindingBuddyHighlightOverlay(Client client, BindingBuddyPlugin plugin, BindingBuddyConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(!plugin.shouldHighlightItem || !config.inventoryFlashToggle()) return null;

        Widget inventoryWidget = client.getWidget(ComponentID.INVENTORY_CONTAINER);
        //todo: Potentially iterate over DynamicChildren like questhelper does looking for the first binding necklace?

        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return null;
        }


        // Check if it's time to toggle the flash
        long currentTime = Instant.now().toEpochMilli();
        if (currentTime - lastFlashTime >= config.flashDuration())
        {
            isHighlightVisible = !isHighlightVisible;
            lastFlashTime = currentTime;
        }


        if(!isHighlightVisible) return null;


        for (Widget item : inventoryWidget.getDynamicChildren())
        {
            if(item.getItemId() == ItemID.BINDING_NECKLACE)

            {
                Rectangle slotBounds = item.getBounds();
                graphics.setColor(config.flashFillColor());
                graphics.fill(slotBounds);
                graphics.setColor(config.flashOutlineColor());
                graphics.draw(slotBounds);
                break;
            }
        }

        return null;

    }
}
