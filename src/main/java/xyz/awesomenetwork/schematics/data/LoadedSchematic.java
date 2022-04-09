package xyz.awesomenetwork.schematics.data;

import java.util.List;

public class LoadedSchematic {
	private final String name;
	private final List<LoadedSchematicBlock> blocks;

	public LoadedSchematic(String name, List<LoadedSchematicBlock> blocks) {
		this.name = name;
		this.blocks = blocks;
	}

	public String getName() {
		return name;
	}

	public List<LoadedSchematicBlock> getBlocks() {
		return blocks;
	}
}
