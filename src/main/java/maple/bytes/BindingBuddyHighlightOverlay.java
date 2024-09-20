package maple.bytes;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;

import java.awt.*;
import java.time.Instant;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;

/*

I studied Quest Helpers code to help implement the below overlay, so credit to the QuestHelper plugin's contributors.

Best I can tell, the code I used was made by: (but there are 197 contributors on that plugin)
 - Lotto
 - Trevor
 - Zoinkwiz

Thanks to all the contributors who worked on Quest Helper, really cool plugin.

*/

public class BindingBuddyHighlightOverlay extends Overlay {

    public final BindingBuddyPlugin plugin;
    public final Client client;
    public final BindingBuddyConfig config;

    private long lastFlashTime = 0;
    private boolean isHighlightVisible = true;

    private final Font overlayFont;
    private final String textTop    = "Binding";
    private final String textBottom = "Buddy";
    private FontMetrics metrics;
    private int textTopRelativeX, textBottomRelativeX;  // These will be calculated

    // Inventory slot size constants
    private static final int SLOT_WIDTH = 36;

    // A flag to indicate if we have calculated text positions
    private boolean isTextPositionCalculated = false;

    @Inject
    public BindingBuddyHighlightOverlay(Client client, BindingBuddyPlugin plugin, BindingBuddyConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.overlayFont = new Font("Arial", Font.BOLD, 10);
    }



    // Calculate text positioning relative to the slot size using Graphics2D
    private void calculateTextRelativePosition(Graphics2D graphics) {
        graphics.setFont(overlayFont);
        metrics = graphics.getFontMetrics();

        // Calculate relative X positions based on the fixed width of the slot (36x32)
        textTopRelativeX = (SLOT_WIDTH - metrics.stringWidth(textTop)) / 2;
        textBottomRelativeX = (SLOT_WIDTH - metrics.stringWidth(textBottom)) / 2;

        // Mark that we've calculated these values
        isTextPositionCalculated = true;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        //Config devBit to enable GUI element testing.
        if(!config.devBit()){
            if(!plugin.shouldHighlightItem || !config.inventoryFlashToggle()) return null;
        }

        Widget inventoryWidget = client.getWidget(ComponentID.INVENTORY_CONTAINER);

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

        Widget item = inventoryWidget.getChild(plugin.bindingInvIndex);

        if(item == null) return null;

        if(item.getItemId() != ItemID.BINDING_NECKLACE) return null;

        Rectangle slotBounds = item.getBounds();

        // Fill the slot with a flash color
        graphics.setColor(config.flashFillColor());
        graphics.fill(slotBounds);

        // Draw the outline
        graphics.setColor(config.flashOutlineColor());
        graphics.draw(slotBounds);


        graphics.setFont(overlayFont);
        graphics.setColor(config.inventoryFlashFontColor());

        // Calculate text positioning if it hasn't been done yet
        if (!isTextPositionCalculated) {
            calculateTextRelativePosition(graphics);
        }

        // Calculate dynamic X positions based on current slot position
        int textTopX = slotBounds.x + textTopRelativeX;
        int textBottomX = slotBounds.x + textBottomRelativeX;

        // Draw "Binding" above the item
        graphics.drawString(textTop, textTopX, slotBounds.y - 1);

        // Draw "Buddy" below the item
        graphics.drawString(textBottom, textBottomX, slotBounds.y + slotBounds.height + 9);





        return null;

    }
}
