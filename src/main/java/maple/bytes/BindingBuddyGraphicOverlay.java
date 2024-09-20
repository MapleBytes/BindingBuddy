package maple.bytes;


import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;




/*

I used significant portions of code from https://github.com/mathewchapman to implement this graphical overlay

So, thank you mathewchapman for being an absolute legend.

*/

public class BindingBuddyGraphicOverlay extends Overlay {

    private static final ScaledImage previouslyScaledImage = new ScaledImage();
    private static BufferedImage protectItemImage;
    private final Client client;
    private final BindingBuddyPlugin plugin;
    private final BindingBuddyConfig config;

    private Font overlayFont;
    private int textX, textY, textWidth, textHeight;
    private final String overlayText = "Binding Buddy";


    @Inject
    BindingBuddyGraphicOverlay(BindingBuddyPlugin plugin, BindingBuddyConfig config, Client client) throws PluginInstantiationException {
        super(plugin);
        setPriority(0.2F);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        loadProtectItemImage();
        previouslyScaledImage.scale = 1;
        previouslyScaledImage.scaledBufferedImage = protectItemImage;
        this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Edit Config", "Binding Buddy"));

        initializeFontAndPositioning();
    }


    public void initializeFontAndPositioning() {
        overlayFont = new Font("Arial", Font.BOLD, 8);

        // Recalculate text positioning when called
        calculateTextPositioning();
    }

    // Calculate text positioning based on the current font and image size
    private void calculateTextPositioning() {
        BufferedImage image = previouslyScaledImage.scaledBufferedImage;  // Ensure we have the current scaled image
        if (image == null) return;

        FontMetrics metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                .createGraphics()
                .getFontMetrics(overlayFont);  // Use a dummy graphics object to get FontMetrics
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        textWidth = metrics.stringWidth(overlayText);
        textHeight = metrics.getHeight();
        textX = (imageWidth - textWidth) / 2;
        textY = (int) (imageHeight * 0.25);  // Slightly above the bottom edge
    }

    private static void loadProtectItemImage() {
        protectItemImage = ImageUtil.loadImageResource(BindingBuddyPlugin.class, "/BindingNecklaceNotEquipped.png");
    }


    private BufferedImage scaleImage(BufferedImage protectItemImage) {
        if (previouslyScaledImage.scale == config.scale() || config.scale() <= 0) {
            initializeFontAndPositioning();
            return previouslyScaledImage.scaledBufferedImage;
        }
        int w = protectItemImage.getWidth();
        int h = protectItemImage.getHeight();
        BufferedImage scaledProtectItemImage =
                new BufferedImage(
                        config.scale() * w, config.scale() * h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(config.scale(), config.scale());
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledProtectItemImage = scaleOp.filter(protectItemImage, scaledProtectItemImage);
        previouslyScaledImage.scaledBufferedImage = scaledProtectItemImage;
        previouslyScaledImage.scale = config.scale();
        return scaledProtectItemImage;
    }

    private static class ScaledImage {
        private int scale;
        private BufferedImage scaledBufferedImage;
    }


    @Override
    public Dimension render(Graphics2D graphics) {

        if(!config.devBit()) {
            //todo: Test if the toggle is working.
            if (!plugin.shouldDisplayImage || !config.graphicOverlayToggle()) return null;
        }

        Widget inventoryWidget = client.getWidget(ComponentID.INVENTORY_CONTAINER);
        Widget pinScreen       = client.getWidget(ComponentID.BANK_PIN_CONTAINER);


        if(inventoryWidget == null || pinScreen != null) return null;


        BufferedImage scaledProtectItemImage = scaleImage(protectItemImage);
        ImageComponent imagePanelComponent = new ImageComponent(scaledProtectItemImage);
        Dimension dimension = imagePanelComponent.render(graphics);

        //Updated.
        graphics.setFont(overlayFont);  // Adjust the font size and type as needed
        graphics.setColor(config.graphicOverlayTextFontColor());  // Set the color of the text, e.g., white

        // Get the original composite to restore after setting opacity
        Composite originalComposite = graphics.getComposite();

        // Set the opacity (alpha). 0.0f is fully transparent, 1.0f is fully opaque
        float opacity = 0.5f;  // Example: 50% opacity
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        graphics.drawString(overlayText, textX, textY);

        // Restore the original composite (important to avoid affecting other rendering operations)
        graphics.setComposite(originalComposite);




        return dimension;
    }
}
