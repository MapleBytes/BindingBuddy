package maple.bytes;

import dev.maple.CoreToolKitPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BindingBuddyPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BindingBuddyPlugin.class, CoreToolKitPlugin.class);
		RuneLite.main(args);
	}
}