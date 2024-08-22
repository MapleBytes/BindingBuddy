package maple.bytes;

import dev.maple.CoreToolKitDataManager;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;

import net.runelite.api.ItemContainer;



@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class BindingBuddyPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BindingBuddyConfig config;

	@Inject
	private CoreToolKitDataManager coreToolKitDataManager;


	private final int INITIAL_MAGIC_IMBUE_VARBIT_DURATION = 2;
	private final int INVENTORY_CONTAINER_ID = 93;
	private final int EQUIPMENT_CONTAINER_ID = 94;

	private final String BINDING_NECKLACE_BROKEN_CHAT_MESSAGE = "Your Binding necklace has disintegrated.";

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		coreToolKitDataManager.setData("Hello","5");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}






	//Functions


	//Function to manually grab the players Inventory Container then pass it to the handler
	public void manualHandleInventoryContainer()
	{
		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

		handleInventoryContainer(container);
	}


	//Function to manually grab the players Equipment Container then pass it to the handler
	public void manualHandleEquipmentContainer()
	{
		ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);

		handleEquipmentContainer(container);
	}


	public void handleInventoryContainer(ItemContainer inventoryItemContainer)
	{

	}

	public void handleEquipmentContainer(ItemContainer inventoryEquipmentContainer)
	{

	}








	//Hooks
	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		//<col=7f007f>Your Binding necklace has disintegrated.</col>
		//GAMEMESSAGE

		//Check if message is GAMEMESSAGE, if not escape.
		if(chatMessage.getType() != ChatMessageType.GAMEMESSAGE) return;

		//Check for Binding Necklace Broken substring, if not escape.
		if(!chatMessage.getMessage().contains(BINDING_NECKLACE_BROKEN_CHAT_MESSAGE)) return;

		//TODO: Set ShouldHighlightItem Flag.

	}

	@Subscribe
	public void onContainerChanged(ItemContainerChanged itemContainerChanged)
	{




		if(   itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()
			||itemContainerChanged.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			switch(itemContainerChanged.getContainerId())
			{
				case INVENTORY_CONTAINER_ID:
					handleInventoryContainer(itemContainerChanged.getItemContainer());
				case EQUIPMENT_CONTAINER_ID:
					handleEquipmentContainer(itemContainerChanged.getItemContainer());
				default:
					//TODO: Potentially make this more polite, and a better message on release!
					log.warn("FOOBAR LOGIC IN ON CONTAINER CHANGED, GET SMART MAPLE");

			}
		}









		if(itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId())
		{
			handleInventoryContainer(itemContainerChanged.getItemContainer());
		}

		if(itemContainerChanged.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			handleEquipmentContainer(itemContainerChanged.getItemContainer());
		}


	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if(varbitChanged.getVarbitId() != Varbits.MAGIC_IMBUE) return;


		//2 represents the initial value of
		if(varbitChanged.getValue() == INITIAL_MAGIC_IMBUE_VARBIT_DURATION)
		{
			//TODO: Implement magic imbue do stuff here, as this is the detection for magic imbue being cast!
		}


	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	BindingBuddyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BindingBuddyConfig.class);
	}
}
