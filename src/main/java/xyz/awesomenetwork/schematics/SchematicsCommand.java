package xyz.awesomenetwork.schematics;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;
import xyz.awesomenetwork.schematics.enums.SchematicMetadata;

import java.io.IOException;

public class SchematicsCommand implements CommandExecutor {
	private final Plugin plugin;
	private final SchematicHandler schematicHandler;

	public SchematicsCommand(Plugin plugin, SchematicHandler schematicHandler) {
		this.plugin = plugin;
		this.schematicHandler = schematicHandler;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().contentEquals("schematics")) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cannot execute as console!");
			return true;
		}

		Player player = (Player) sender;
		String subCommand = args.length > 0 ? args[0] : "";

		switch (subCommand.toLowerCase()) {
		// Toggle select mode
		case "select":
			// Set or remove metadata that the interact listener uses to detect whether to set selection points
			if (player.hasMetadata(SchematicMetadata.SELECTION_MODE.name())) {
				player.removeMetadata(SchematicMetadata.SELECTION_MODE.name(), plugin);
				player.removeMetadata(SchematicMetadata.SELECTION_POINT_1.name(), plugin);
				player.removeMetadata(SchematicMetadata.SELECTION_POINT_2.name(), plugin);
				player.sendMessage("Selection mode disabled.");
			} else {
				player.setMetadata(SchematicMetadata.SELECTION_MODE.name(), new FixedMetadataValue(plugin, null));
				player.sendMessage("Selection mode enabled.");
			}
			break;

		// Create a schematic based on current selection
		case "create":
			if (!player.hasMetadata(SchematicMetadata.SELECTION_MODE.name())) {
				player.sendMessage("Please enable selection mode and select both points first (left and right click).");
				player.sendMessage("/schematics select");
				break;
			}

			if (!player.hasMetadata(SchematicMetadata.SELECTION_POINT_1.name()) || !player.hasMetadata(SchematicMetadata.SELECTION_POINT_2.name())) {
				player.sendMessage("Please select both points first (left and right click).");
				break;
			}

			if (args.length < 2) {
				player.sendMessage("Please specify a name for the schematic");
				player.sendMessage("/" + label + " create <name>");
				break;
			}

			String schematicNameCreate = args[1];

			player.sendMessage("Creating schematic...");

			Location point1 = (Location) player.getMetadata(SchematicMetadata.SELECTION_POINT_1.name()).get(0).value();
			Location point2 = (Location) player.getMetadata(SchematicMetadata.SELECTION_POINT_2.name()).get(0).value();

			player.removeMetadata(SchematicMetadata.SELECTION_POINT_1.name(), plugin);
			player.removeMetadata(SchematicMetadata.SELECTION_POINT_2.name(), plugin);

			// Strip world from location
			LocationNoWorld centre = new LocationNoWorld(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

			// Load schematic from world blocks
			LoadedSchematic loadedSchematicCreate = schematicHandler.createSchematic(schematicNameCreate, centre, point1, point2);
			try {
				// Save loaded schematic to disk
				schematicHandler.saveSchematic(loadedSchematicCreate);
			} catch (IOException e) {
				e.printStackTrace();
				player.sendMessage("Failed to create schematic, see console for stacktrace");
				break;
			}

			player.sendMessage("Created schematic \"" + schematicNameCreate + "\"!");
			break;

		case "paste":
			if (args.length < 2) {
				player.sendMessage("Please specify a schematic name");
				player.sendMessage("/" + label + " paste <name> [rotation degrees]");
				break;
			}

			String schematicNamePaste = args[1];

			int rotationDegrees = 0;
			if (args.length >= 3) {
				try {
					rotationDegrees = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					player.sendMessage("Rotation requires a number, not \"" + args[2] + "\"!");
					break;
				}
			}

			// Ticks to complete is how much time it should take to paste the schematic
			int ticksToComplete = 0;
			if (args.length >= 4) {
				try {
					ticksToComplete = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage("Ticks to complete requires a number, not \"" + args[3] + "\"!");
					break;
				}
			}

			LoadedSchematic loadedSchematicPaste;
			try {
				// Schematic names are all saved in lowercase so if someone types it accidentally in CAPS they don't get stuck for an hour wondering why their schematic didn't load...
				loadedSchematicPaste = schematicHandler.loadSchematic(schematicNamePaste.toLowerCase());
			} catch (IOException e) {
				e.printStackTrace();
				player.sendMessage("Failed to load schematic, see console for stacktrace");
				break;
			}

			SchematicPasteCallback callback = new SchematicPasteCallback() {
				@Override
				public void finished(String pasteId) {
					if (player.isOnline()) {
						player.sendMessage("Pasted schematic \"" + loadedSchematicPaste.getName() + "\"!");
					}
				}
			};

			player.sendMessage("Pasting schematic...");
			SchematicPasteOptions options = new SchematicPasteOptions(loadedSchematicPaste, player.getLocation(), rotationDegrees, callback, ticksToComplete);
			schematicHandler.pasteSchematic(options);
			break;

		default:
			player.sendMessage("Schematics arguments:");
			player.sendMessage("/" + label + " select");
			player.sendMessage("/" + label + " create <name>");
			player.sendMessage("/" + label + " paste <name> [rotation degrees] [time in ticks]");
			break;
		}

		return true;
	}
}
