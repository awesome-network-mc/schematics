package xyz.awesomenetwork.schematics;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public interface SchematicPasteCallback {
    default boolean blockPaste(String pasteId, BlockData block, Location centre, LocationNoWorld relativeLocation) {
        return true;
    };
    default void finished(String pasteId) {

    };
}
