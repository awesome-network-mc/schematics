package xyz.awesomenetwork.schematics;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.schematics.data.LoadedSchematicBlock;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;
import xyz.awesomenetwork.schematics.json.Schematic;
import xyz.awesomenetwork.schematics.json.SchematicBlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class SchematicHandler {

	private final JavaPlugin plugin;
	private final String schematicDataFolder;

	// Blocks that require another block to exist, so they need to be pasted after everything else
	private final Set<Material> PICKY_BLOCKS = new HashSet<Material>() {{
		add(Material.ACACIA_SIGN);
		add(Material.ACACIA_WALL_SIGN);
		add(Material.BIRCH_SIGN);
		add(Material.SPRUCE_SIGN);
		add(Material.BIRCH_WALL_SIGN);
		add(Material.CRIMSON_SIGN);
		add(Material.CRIMSON_WALL_SIGN);
		add(Material.DARK_OAK_SIGN);
		add(Material.DARK_OAK_WALL_SIGN);
		add(Material.SPRUCE_WALL_SIGN);
		add(Material.JUNGLE_SIGN);
		add(Material.JUNGLE_WALL_SIGN);
		add(Material.OAK_SIGN);
		add(Material.OAK_WALL_SIGN);
		add(Material.WARPED_SIGN);
		add(Material.WARPED_WALL_SIGN);
		add(Material.LADDER);
		add(Material.VINE);
		add(Material.CAVE_VINES);
		add(Material.CAVE_VINES_PLANT);
		add(Material.POPPY);
		add(Material.DANDELION);
		add(Material.BLUE_ORCHID);
		add(Material.ALLIUM);
		add(Material.AZURE_BLUET);
		add(Material.RED_TULIP);
		add(Material.ORANGE_TULIP);
		add(Material.WHITE_TULIP);
		add(Material.PINK_TULIP);
		add(Material.OXEYE_DAISY);
		add(Material.CORNFLOWER);
		add(Material.LILY_OF_THE_VALLEY);
		add(Material.WITHER_ROSE);
		add(Material.SUNFLOWER);
		add(Material.LILAC);
		add(Material.ROSE_BUSH);
		add(Material.PEONY);
		add(Material.WATER);
		add(Material.LAVA);
		add(Material.GRAVEL);
		add(Material.SAND);
		add(Material.COCOA);
	}};

	private final Map<String, Integer> runningPasteTasks = new HashMap<>();

	public SchematicHandler(JavaPlugin plugin, String schematicDataFolder) {
		this.plugin = plugin;
		this.schematicDataFolder = schematicDataFolder;
	}

	private String getSchematicPath(String fileName) {
		if (!fileName.endsWith(".json")) fileName += ".json";
		String saveDir = schematicDataFolder.endsWith("/") ? schematicDataFolder : schematicDataFolder + "/";
		return saveDir + fileName;
	}

	public LoadedSchematic loadSchematic(String fileName) throws IOException {
		String path = getSchematicPath(fileName.toLowerCase());

		File schematicFile = new File(path);
		if (!schematicFile.exists()) throw new FileNotFoundException(path);

		String fileContents = new String(Files.readAllBytes(schematicFile.toPath()));

		Gson gson = new Gson();
		Schematic schematic = gson.fromJson(fileContents, Schematic.class);

		return convertJsonToLoadedSchematic(schematic);
	}

	private LoadedSchematic convertJsonToLoadedSchematic(Schematic schematic) {
		List<LoadedSchematicBlock> blocks = new ArrayList<>();
		List<LoadedSchematicBlock> pickyBlocks = new ArrayList<>();

		// Sort blocks so start of the array is lowest Y coordinate and end of the array is highest Y coordinate, useful in Sky Royale when the islands crumble
		int lowestY = Integer.MAX_VALUE;
		int highestY = Integer.MIN_VALUE;
		for (SchematicBlock schematicBlock : schematic.blocks) {
			if (schematicBlock.y < lowestY) lowestY = schematicBlock.y;
			if (schematicBlock.y > highestY) highestY = schematicBlock.y;
		}
		for (int y = lowestY; y <= highestY; y++) {
			for (SchematicBlock schematicBlock : schematic.blocks) {
				if (schematicBlock.y == y) {
					LocationNoWorld relativeLocation = new LocationNoWorld(schematicBlock.x, schematicBlock.y, schematicBlock.z);
					LoadedSchematicBlock block = new LoadedSchematicBlock(relativeLocation, plugin.getServer().createBlockData(schematicBlock.serialised));

					if (PICKY_BLOCKS.contains(block.getBlockData().getMaterial())) pickyBlocks.add(block);
					else blocks.add(block);
				}
			}
		}

		blocks.addAll(pickyBlocks);

		return new LoadedSchematic(schematic.name, blocks);
	}

	// Convert schematic for storage in JSON file using GSON
	private Schematic convertLoadedSchematicToJson(LoadedSchematic loadedSchematic) {
		List<SchematicBlock> blocks = new ArrayList<>();
		for (LoadedSchematicBlock block : loadedSchematic.getBlocks()) {
			SchematicBlock schematicBlock = new SchematicBlock();

			schematicBlock.serialised = block.getBlockData().getAsString();

			LocationNoWorld location = block.getRelativeLocation();
			schematicBlock.x = location.getX();
			schematicBlock.y = location.getY();
			schematicBlock.z = location.getZ();

			blocks.add(schematicBlock);
		}

		Schematic schematic = new Schematic();
		schematic.name = loadedSchematic.getName();
		schematic.blocks = blocks.toArray(new SchematicBlock[0]);

		return schematic;
	}

	public LoadedSchematic createSchematic(String name, LocationNoWorld centre, Location point1, Location point2) {
		int xStart = point1.getBlockX();
		int xStop = point2.getBlockX();
		if (point2.getBlockX() < xStart) {
			xStop = xStart;
			xStart = point2.getBlockX();
		}

		int yStart = point1.getBlockY();
		int yStop = point2.getBlockY();
		if (point2.getBlockY() < yStart) {
			yStop = yStart;
			yStart = point2.getBlockY();
		}

		int zStart = point1.getBlockZ();
		int zStop = point2.getBlockZ();
		if (point2.getBlockZ() < zStart) {
			zStop = zStart;
			zStart = point2.getBlockZ();
		}

		World world = point1.getWorld();

		Set<SchematicBlock> blocks = new HashSet<>();
		for (int worldX = xStart; worldX <= xStop; worldX++) {
			for (int worldY = yStart; worldY <= yStop; worldY++) {
				for (int worldZ = zStart; worldZ <= zStop; worldZ++) {

					int relativeX = worldX - (int) Math.floor(centre.getX());
					int relativeY = worldY - (int) Math.floor(centre.getY());
					int relativeZ = worldZ - (int) Math.floor(centre.getZ());

					LocationNoWorld location = new LocationNoWorld(relativeX, relativeY, relativeZ);
					BlockData blockData = world.getBlockAt(worldX, worldY, worldZ).getBlockData();

					if (blockData.getMaterial() != Material.AIR) {
						SchematicBlock block = new SchematicBlock();
						block.x = location.getX();
						block.y = location.getY();
						block.z = location.getZ();
						block.serialised = blockData.getAsString();
						blocks.add(block);
					}
				}
			}
		}

		Schematic schematic = new Schematic();
		schematic.name = name;
		schematic.blocks = blocks.toArray(new SchematicBlock[0]);

		return convertJsonToLoadedSchematic(schematic);
	}

	public boolean saveSchematic(LoadedSchematic loadedSchematic) throws IOException {
		String path = getSchematicPath(loadedSchematic.getName());

		File schematicFile = new File(path);
		if (schematicFile.exists()) return false;

		Gson gson = new Gson();
		String schematic = gson.toJson(convertLoadedSchematicToJson(loadedSchematic));
		Files.write(schematicFile.toPath(), schematic.getBytes());

		return true;
	}

	public void pasteSchematic(LoadedSchematic schematic, Location centre) {
		SchematicPasteOptions options = new SchematicPasteOptions(schematic, centre);
		pasteSchematic(options);
	}

	// SchematicPasteOptions exists because the ticksToComplete can be modified mid-paste, so an object reference needs to be passed rather than a primitive
	public void pasteSchematic(SchematicPasteOptions options) {
		// Generate an ID so the repeating task can be stopped from within the task itself
		final String id = UUID.randomUUID().toString();

		// I think this is radians, the internet people told me so
		final double radian = options.getRotationDegrees() * (Math.PI / 180);

		Location centre = options.getCentre();
		World world = options.getCentre().getWorld();
		int centreX = centre.getBlockX();
		int centreY = centre.getBlockY();
		int centreZ = centre.getBlockZ();

		LoadedSchematic schematic = options.getSchematic();
		final int totalBlockCount = schematic.getBlocks().size();
		final Iterator<LoadedSchematicBlock> it = schematic.getBlocks().iterator();
		final SchematicPasteCallback callback = options.getCallback();

		newPasteTask(id, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			double blockPasteAmount = 0.0;

			public void run() {
				int schematicCompletionTime = options.getTicksToComplete();
				double blocksPerTick = schematicCompletionTime > 0 ? (double) totalBlockCount / (double) schematicCompletionTime : totalBlockCount;
				blockPasteAmount += blocksPerTick;

				int blockPasteAmountInt = (int) Math.floor(blockPasteAmount);
				blockPasteAmount -= blockPasteAmountInt;
	
				for (int i = 0; i < blockPasteAmountInt; i++) {
					if (!it.hasNext() || options.isCancelled()) {
						cancelPasteTask(id);
						if (callback != null) {
							callback.finished(id);
						}
						return;
					}
	
					LoadedSchematicBlock data = it.next();
					BlockData blockData = data.getBlockData();
					LocationNoWorld relativeLocation = data.getRelativeLocation();
	
					int finalX = centreX + (int) Math.round((relativeLocation.getX() * Math.cos(radian)) - (relativeLocation.getZ() * Math.sin(radian)));
					int finalY = centreY + relativeLocation.getY();
					int finalZ = centreZ + (int) Math.round((relativeLocation.getZ() * Math.cos(radian)) + (relativeLocation.getX() * Math.sin(radian)));
					Location blockLocation = new Location(world, finalX, finalY, finalZ);
	
					Block block = world.getBlockAt(finalX, finalY, finalZ);
	
					if (callback != null) {
						// Optional callback to get what block has been pasted, and also to stop this block pasting if it returns false
						if (!callback.prePaste(id, blockData, centre, blockLocation, relativeLocation)) continue;
					}
	
					block.setBlockData(blockData);
	
					if (callback != null) {
						callback.postPaste(id, block, centre, blockLocation, relativeLocation);
					}
				}
			}
		}, 0, 1));
	}

	private void newPasteTask(String id, int taskId) {
		if (runningPasteTasks.containsKey(id)) cancelPasteTask(id);
		runningPasteTasks.put(id, taskId);
	}

	private void cancelPasteTask(String id) {
		plugin.getServer().getScheduler().cancelTask(runningPasteTasks.get(id));
		runningPasteTasks.remove(id);
	}
}
