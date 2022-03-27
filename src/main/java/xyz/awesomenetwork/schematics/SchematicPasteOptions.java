package xyz.awesomenetwork.schematics;

import org.bukkit.Location;

import xyz.awesomenetwork.schematics.data.LoadedSchematic;

public class SchematicPasteOptions {
    private final LoadedSchematic schematic;
    private final Location centre;
    private final int rotationDegrees;
    private final SchematicPasteCallback callback;

    private int ticksToComplete = 0;

    public SchematicPasteOptions(LoadedSchematic schematic, Location centre, int rotationDegrees, SchematicPasteCallback callback, int ticksToComplete) {
        this.schematic = schematic;
        this.centre = centre;
        this.rotationDegrees = rotationDegrees % 360;
        this.callback = callback;
        this.ticksToComplete = ticksToComplete;
    }
    public SchematicPasteOptions(LoadedSchematic schematic, Location centre) {
        this(schematic, centre, 0, null, 0);
    }

    public LoadedSchematic getSchematic() {
        return schematic;
    }

    public Location getCentre() {
        return centre;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public SchematicPasteCallback getCallback() {
        return callback;
    }

    public int getTicksToComplete() {
        return ticksToComplete;
    }
    public void setTicksToComplete(int ticksToComplete) {
        this.ticksToComplete = ticksToComplete;
    }
}
