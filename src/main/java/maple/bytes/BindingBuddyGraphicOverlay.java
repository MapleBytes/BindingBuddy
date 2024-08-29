package maple.bytes;

import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class BindingBuddyGraphicOverlay extends Overlay {

    private static final ScaledImage previouslyScaledImage = new ScaledImage();
    private static BufferedImage protectItemImage;
    private final BindingBuddyPlugin plugin;
    private final BindingBuddyConfig config;


    @Inject
    BindingBuddyGraphicOverlay(BindingBuddyPlugin plugin, BindingBuddyConfig config) throws PluginInstantiationException {
        super(plugin);
        setPriority(0.5F);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.plugin = plugin;
        this.config = config;
        loadProtectItemImage();
        previouslyScaledImage.scale = 1;
        previouslyScaledImage.scaledBufferedImage = protectItemImage;
    }


    private static void loadProtectItemImage() {
        protectItemImage = ImageUtil.loadImageResource(BindingBuddyPlugin.class, "/BindingNecklaceNotEquipped.png");
    }


    private BufferedImage scaleImage(BufferedImage protectItemImage) {
        if (previouslyScaledImage.scale == config.scale() || config.scale() <= 0) {
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

        //todo: Test if the toggle is working.
        if (!plugin.shouldDisplayImage || !config.graphicOverlayToggle()) return null;


        BufferedImage scaledProtectItemImage = scaleImage(protectItemImage);
        ImageComponent imagePanelComponent = new ImageComponent(scaledProtectItemImage);
        return imagePanelComponent.render(graphics);
    }
}
