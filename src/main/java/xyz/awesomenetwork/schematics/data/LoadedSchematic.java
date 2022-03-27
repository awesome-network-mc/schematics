package xyz.awesomenetwork.schematics.data;

import java.util.ArrayList;

public class LoadedSchematic {
    private final String name;
    private final ArrayList<LoadedSchematicBlock> blocks;

    public LoadedSchematic(String name, ArrayList<LoadedSchematicBlock> blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LoadedSchematicBlock> getBlocks() {
        return blocks;
    }
}
