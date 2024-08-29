package maple.bytes;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("Binding Buddy")
public interface BindingBuddyConfig extends Config
{

	@ConfigSection(
			name = "Graphics Overlay",
			description = "Configuration for the Graphical Overlay",
			position = 1,
			closedByDefault = false
	)
	String graphicOverlayConfiguration = "graphicOverlayConfiguration";

	@ConfigItem(
			keyName = "graphicOverlayToggle",
			name = "Graphic Overlay Toggle",
			description = "A toggle for the graphical binding necklace overlay",
			section = graphicOverlayConfiguration,
			position = 1
	)
	default boolean graphicOverlayToggle(){return true;}

	@ConfigItem(
			keyName = "scale",
			name = "Image scale",
			description = "The scale of the binding necklace image",
			section = graphicOverlayConfiguration,
			position = 2)
	default int scale() {
		return 1;
	}




	@ConfigSection(
			name = "Inventory Highlight",
			description = "Configuration for the inventory highlight",
			position = 2,
			closedByDefault = false
	)
	String inventoryHighlightConfiguration = "inventoryHighlightConfiguration";


	@ConfigItem(
			keyName = "inventoryFlashToggle",
			name = "Inventory Flash Overlay Toggle",
			description = "A toggle for the inventory flash binding necklace overlay",
			section = inventoryHighlightConfiguration,
			position = 1
	)
	default boolean inventoryFlashToggle(){return true;}

	@ConfigItem(
			keyName = "flashDuration",
			name 	= "Flash Duration",
			description = "Interval in milliseconds (500ms = 0.5 seconds) between flashes when highlighting an inventory item",
			section = inventoryHighlightConfiguration,
			position = 2)
	default int flashDuration() {return 200;}

	@Alpha
	@ConfigItem(
			keyName = "flashFillColor",
			name = "Flash Fill Color",
			description = "Configuration for the flash fill color",
			section = inventoryHighlightConfiguration,
			position = 3
	)
	default Color flashFillColor() {return new Color(255, 0, 0, 80);}

	@Alpha
	@ConfigItem(
			keyName = "flashOutlineColor",
			name = "Flash Outline Color",
			description = "Configuration for the flash outline color",
			section = inventoryHighlightConfiguration,
			position = 4
	)
	default Color flashOutlineColor() {return new Color(0, 0, 0, 255);}


}
