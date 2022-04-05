package xyz.awesomenetwork.schematics;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public interface SchematicPasteCallback {
    default boolean prePaste(String pasteId, BlockData blockData, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
        return true;
    };

    default void postPaste(String pasteId, Block block, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
        
    }
    
    default void finished(String pasteId) {

    };
}
