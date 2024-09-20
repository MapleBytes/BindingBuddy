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

	//Will maybe implement in future update.
	@ConfigItem(
			keyName = "graphicOverlayTextToggle",
			name = "Graphic Overlay Text Toggle",
			description = "Enable or disable the Graphic Overlay's identifying text",
			section = graphicOverlayConfiguration,
			position = 3,
			hidden = true,
			secret = true
	)
	default boolean graphicOverlayTextToggle(){return true;}

	//Will maybe implement in future update.
	@ConfigItem(
			keyName = "graphicOverlayTextFontSize",
			name = "Graphic Overlay Text Font Size",
			description = "Customize the font size for the graphical overlay",
			section = graphicOverlayConfiguration,
			position = 4,
			hidden = true,
			secret = true
	)
	default int graphicOverlayTextFontSize(){return 6;}

	//Will maybe implement in future update.
	@Alpha
	@ConfigItem(
			keyName = "graphicOverlayTextFontColor",
			name = "Graphic Overlay Text Font Color",
			description = "Customize the font size for the graphical overlay",
			section = graphicOverlayConfiguration,
			position = 5,
			hidden = true,
			secret = true
	)
	default Color graphicOverlayTextFontColor() {return new Color(255, 255, 255, 255);}





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
	default Color flashFillColor() {return new Color(255, 255, 0, 255);}

	@Alpha
	@ConfigItem(
			keyName = "flashOutlineColor",
			name = "Flash Outline Color",
			description = "Configuration for the flash outline color",
			section = inventoryHighlightConfiguration,
			position = 4
	)
	default Color flashOutlineColor() {return new Color(0, 0, 0, 255);}


	//Will maybe implement in future update.
	@ConfigItem(
			keyName = "inventoryFlashTextToggle",
			name = "Inventory Highlight Text Toggle",
			description = "Enable or disable the inventory highlights identifying text",
			section = inventoryHighlightConfiguration,
			position = 5,
			hidden = true,
			secret = true
	)
	default boolean inventoryFlashTextToggle() {return true;}

	//Will maybe implement in future update.
	@ConfigItem(
			keyName = "inventoryFlashFontSize",
			name = "Flash Font Size",
			description = "Customize the font size of the flash overlay",
			section = inventoryHighlightConfiguration,
			position = 6,
			hidden = true,
			secret = true
	)
	default int inventoryFlashFontSize() {return 10;}

	//Will maybe implement in future update.
	@Alpha
	@ConfigItem(
			keyName = "inventoryFlashFontColor",
			name = "Flash Font Color",
			description = "Customize the font color for the flash overlay",
			section = inventoryHighlightConfiguration,
			position = 7,
			hidden = true,
			secret = true

	)
	default Color inventoryFlashFontColor() {return new Color(255, 255, 255, 255);}



//	@ConfigSection(
//			name = "Debug Settings",
//			description = "Forces the highlight / 2D binding necklace overlays to always show",
//			position = 4,
//			closedByDefault = true
//	)
//	String highlightSection = "highlightSection";
//
//
//
//
//	@ConfigSection(
//			name = "Debug Settings",
//			description = "Forces the highlight / 2D binding necklace overlays to always show",
//			position = 4,
//			closedByDefault = true
//	)
//	String overlaySection = "overlaySection";









	@ConfigSection(
			name = "Debug Settings",
			description = "Forces the highlight / 2D binding necklace overlays to always show",
			position = 4,
			closedByDefault = true
	)
	String debugSection = "debugSection";


	@ConfigItem(
			keyName = "devBit",
			description = "a toggle to force graphical elements to display for testing",
			name = "Development / Dev toggle.",
			section = debugSection

	)
	default boolean devBit(){return false;}




}
