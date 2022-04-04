package xyz.awesomenetwork.schematics;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public interface SchematicPasteCallback {
    default boolean blockPaste(String pasteId, Block block, BlockData blockData, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
        return true;
    };
    default void finished(String pasteId) {

    };
}
