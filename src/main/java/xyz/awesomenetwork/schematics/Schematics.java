package xyz.awesomenetwork.schematics;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.schematics.listeners.PlayerInteractListener;

import java.io.File;

public class Schematics extends JavaPlugin {

	public void onEnable() {
		saveDefaultConfig();

		// Where schematics are saved and loaded from
		String schematicDataFolder = "plugins/" + getName() + "/" + getConfig().getString("schematics_save_relative_directory");
		new File(schematicDataFolder).mkdirs();

		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);

		SchematicHandler schematicHandler = new SchematicHandler(this, schematicDataFolder);
		getServer().getServicesManager().register(SchematicHandler.class, schematicHandler, this, ServicePriority.Normal);

		getCommand("schematics").setExecutor(new SchematicsCommand(this, schematicHandler));
	}

}
