package maple.bytes;

import com.google.common.collect.ImmutableSet;



import dev.maple.CoreToolKitDataManager;


import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.api.ItemContainer;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Set;


@Slf4j
@PluginDescriptor(
	name = "BindingBuddy"
)
public class BindingBuddyPlugin extends Plugin
{
	@Inject
	public Client client;

	@Inject
	public BindingBuddyConfig config;

	@Inject
	public OverlayManager overlayManager;

	@Inject
	public BindingBuddyHighlightOverlay bindingBuddyHighlightOverlay;

	@Inject
	public CoreToolKitDataManager coreToolKitDataManager;

	@Inject
	public BindingBuddyGraphicOverlay bindingBuddyGraphicOverlay;


	private static final int 	 INVENTORY_CONTAINER_ID = 93;
	private static final int 	 EQUIPMENT_CONTAINER_ID = 94;

	//Global Boolean Flags
	public boolean 	shouldDisplayImage	 = false;
	public boolean 	shouldHighlightItem	 = false;
	public boolean 	bindingEquipped		 = false;
	public boolean 	bindingInInventory 	 = false;
	public boolean 	inAltarRegion		 = false;
	public boolean	magicImbueEffectUsed = false;;


	private static final Set<Integer> ALTAR_REGION_IDS = ImmutableSet.of(
		11339, //Air Altar
		10827, //Water Altar
		10571, //Earth Altar
		10315  //Fire Altar
	);

	private static final Set<Integer> ELEMENTAL_RUNE_IDS = ImmutableSet.of(
		556, 	//Air runes
		557, 	//Earth Runes
		555,	//Water Runes
		554  	//Fire Runes
	);


	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		overlayManager.add(bindingBuddyHighlightOverlay);
		overlayManager.add(bindingBuddyGraphicOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(bindingBuddyHighlightOverlay);
		overlayManager.remove(bindingBuddyGraphicOverlay);
	}






	//Functions


	//Function to manually grab the players Inventory Container then pass it to the handler
	public void manualHandleInventoryContainer()
	{
		coreToolKitDataManager.setData("manualHandleInventoryContainer"," #");
		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

		handleInventoryContainer(container);
	}


	//Function to manually grab the players Equipment Container then pass it to the handler
	public void manualHandleEquipmentContainer()
	{
		coreToolKitDataManager.setData("manualHandleEquipmentContainer"," #");
		ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);

		handleEquipmentContainer(container);
	}



	//Function to find
	//todo: can the logic be separated into more function calls?


	public void handleInventoryContainer(ItemContainer inventoryItemContainer)
	{

		coreToolKitDataManager.setData("handleInventoryContainer"," #");
		//Check if inventory container is null, escape if so.
		if(inventoryItemContainer == null) return;



		boolean elementalRunesFound 	= false;
		coreToolKitDataManager.setData("elementalRunesFound",String.valueOf(elementalRunesFound));

		boolean bindingNecklaceFound 	= false;
		coreToolKitDataManager.setData("bindingNecklaceFound",String.valueOf(bindingNecklaceFound));




		Item[] items = inventoryItemContainer.getItems();

		//Iterate over inventory and find location of (if any) elemental runes, and binding necklace
		for (int i = 0; i < items.length; i++) {

			if(elementalRunesFound && bindingNecklaceFound) break;

			Item item = items[i];
			var itemID = item.getId();

			//we only want the first binding necklace in the inventory, so we don't do this check
			//if we have already found one.
			if(!bindingNecklaceFound && itemID == ItemID.BINDING_NECKLACE) {
					bindingInInventory = true;
					coreToolKitDataManager.setData("bindingInInventory",String.valueOf(bindingInInventory));
					bindingNecklaceFound = true;
					coreToolKitDataManager.setData("bindingNecklaceFound",String.valueOf(bindingNecklaceFound));
			}

			if(elementalRunesFound) continue;

			if(ELEMENTAL_RUNE_IDS.contains(itemID))
			{
				elementalRunesFound = true;
				coreToolKitDataManager.setData("elementalRunesFound",String.valueOf(elementalRunesFound));
			}

		}

		//if we did not locate a bindingNecklace during our loop, we need to update the global bindingInInventory.
		//and ensure that shouldHighlightItem is set to False.
		if(!bindingNecklaceFound){
			bindingInInventory = false;
			coreToolKitDataManager.setData("bindingInInventory",String.valueOf(bindingInInventory));
			shouldHighlightItem = false;
			coreToolKitDataManager.setData("shouldHighlightItem",String.valueOf(shouldHighlightItem));
		}





		//If we located elemental runes in the players inventory, we check to see if they are under the effects of
		//magic imbue, and are inside an altar region. If so we update the global magicImbueEffectUsed boolean.
		if(elementalRunesFound && inAltarRegion && client.getVarbitValue(Varbits.MAGIC_IMBUE) != 0)
		{
			coreToolKitDataManager.setData("RunesFoundInsideAltarRegion"," #");
			magicImbueEffectUsed = true;
			coreToolKitDataManager.setData("magicImbueEffectUsed",String.valueOf(magicImbueEffectUsed));
		}


		//if we did not find elemental runes, we set magicImbueEffectUsed back to false.
		//I chose to do this, because the only way I can really determine if a player is doing combos
		//is if they were under the effect of magic imbue, while inside an altar region with elemental runes in
		//their inventory. Once they don't have the elemental runes, I can no longer proceed with that assumption.
		if (!elementalRunesFound)
		{
			magicImbueEffectUsed = false;
			coreToolKitDataManager.setData("magicImbueEffectUsed",String.valueOf(magicImbueEffectUsed));
		}


		handleShouldDisplayLogic();

	}



	public void handleShouldDisplayLogic()
	{
		//Determining if we should display one of the overlays. Requires magicImbueEffect = true;

		if(!magicImbueEffectUsed) return;

		//If the player is already wearing a binding necklace, none of the following matters. as we don't notify.
		if(bindingEquipped)
		{
			shouldDisplayImage = false;
			coreToolKitDataManager.setData("shouldDisplayImage",String.valueOf(shouldDisplayImage));
			shouldHighlightItem = false;
			coreToolKitDataManager.setData("shouldHighlightItem",String.valueOf(shouldHighlightItem));
			return;
		}


		//should only be reachable if binding necklace is not equipped.
		//If we have one available in the players inventory we highlight it.
		if(bindingInInventory)
		{
			shouldHighlightItem = true;
			coreToolKitDataManager.setData("shouldHighlightItem",String.valueOf(shouldHighlightItem));
			shouldDisplayImage  = false;
			coreToolKitDataManager.setData("shouldDisplayImage",String.valueOf(shouldDisplayImage));
			return;
		}


		/*
		Now that we have checked if the binding is equipped, and if we have one in our inventory.
		The only remaining possibility is that we do not have one equipped, and do not have on in our inventory.
		I still do logical checks for these conditions, so that I can have a "FOOBARLOGIC" block.
		 */

		if(!bindingInInventory && !bindingEquipped)
		{
			shouldDisplayImage = true;
			coreToolKitDataManager.setData("shouldDisplayImage",String.valueOf(shouldDisplayImage));
			return;
		}



		log.warn("FOOBARLOGIC in InventoryHandler, GET GOOD MAPLE.");
	}


	public void handleEquipmentContainer(ItemContainer inventoryEquipmentContainer)
	{
		coreToolKitDataManager.setData("handleEquipmentContainer"," #");


		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			bindingEquipped = false;
			coreToolKitDataManager.setData("bindingEquipped",String.valueOf(bindingEquipped));
		} else
		{
			final Item neck = equipment.getItem(EquipmentInventorySlot.AMULET.getSlotIdx());
			bindingEquipped = neck != null && ItemID.BINDING_NECKLACE == neck.getId();
			coreToolKitDataManager.setData("bindingEquipped",String.valueOf(bindingEquipped));
		}

		handleShouldDisplayLogic();
	}








	//Hooks

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		coreToolKitDataManager.setData("onItemContainerChanged"," #");
		//If the player is not inside an altar region we escape.
		if(!inAltarRegion) return;
		
		if(   itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()
			||itemContainerChanged.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			switch(itemContainerChanged.getContainerId())
			{

				case EQUIPMENT_CONTAINER_ID:
					handleEquipmentContainer(itemContainerChanged.getItemContainer());
					break;


				case INVENTORY_CONTAINER_ID:
					handleInventoryContainer(itemContainerChanged.getItemContainer());
					break;

				default:
					//TODO: Potentially make this more polite, and a better message on release!
					log.warn("FOOBAR LOGIC IN ON CONTAINER CHANGED, GET SMART MAPLE");
					break;

			}
		}

	}


	@Subscribe
	public void onWidgetClosed(WidgetClosed widgetClosed)
	{

		//We turn off both highlighting and the 2D graphic when a bank interface is closed.
		//It's difficult to detect if the player has stopped doing combinations runes, so I opted to just
		//Turn off the overlays / highlights on bank interface closed, and once that player next enters an altar region
		//It will just re-display them.

		if(widgetClosed.getGroupId() != InterfaceID.BANK) return;
		coreToolKitDataManager.setData("onWidgetClosed"," #");

		shouldDisplayImage 	= false;
		coreToolKitDataManager.setData("shouldDisplayImage",String.valueOf(shouldDisplayImage));
		shouldHighlightItem = false;
		coreToolKitDataManager.setData("shouldHighlightItem",String.valueOf(shouldHighlightItem));

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		coreToolKitDataManager.setData("onGameStateChanged"," #");
		//If gamestate is not LOGGED_IN escape.
		if(!gameStateChanged.getGameState().equals(GameState.LOGGED_IN)) return;

		//get the local players Region ID.
		Player player = client.getLocalPlayer();
		int regionId = player.getWorldLocation().getRegionID();


		//if we are not inside an altar region, set inAltarRegion to false, and escape.
		if(!ALTAR_REGION_IDS.contains(regionId))
		{

			inAltarRegion = false;
			coreToolKitDataManager.setData("inAltarRegion",String.valueOf(inAltarRegion));
			return;
		}

		//from here we can be pretty confident that we are inside an altar region.
		inAltarRegion = true;
		coreToolKitDataManager.setData("inAltarRegion",String.valueOf(inAltarRegion));



		//TODO: Does order of operations matter with these two functions?
		//TODO: I think it really really does matter.
		//call the manual handlers for InventoryContainer, and Equipment container.
		manualHandleInventoryContainer();
		manualHandleEquipmentContainer();

	}

	@Provides
	BindingBuddyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BindingBuddyConfig.class);
	}
}
