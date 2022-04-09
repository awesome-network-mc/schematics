package xyz.awesomenetwork.schematics.data;

import org.bukkit.block.data.BlockData;

public class LoadedSchematicBlock {
	private final LocationNoWorld location;
	private final BlockData blockData;

	public LoadedSchematicBlock(LocationNoWorld location, BlockData blockData) {
		this.location = location;
		this.blockData = blockData;
	}

	public LocationNoWorld getRelativeLocation() {
		return location;
	}

	public BlockData getBlockData() {
		return blockData;
	}
}
