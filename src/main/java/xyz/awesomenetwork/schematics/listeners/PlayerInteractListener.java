package xyz.awesomenetwork.schematics.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import xyz.awesomenetwork.schematics.enums.SchematicMetadata;

public class PlayerInteractListener implements Listener {
	private final Plugin plugin;

	public PlayerInteractListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (!player.hasMetadata(SchematicMetadata.SELECTION_MODE.name())) return;
		if (e.getClickedBlock() == null) return;

		e.setCancelled(true);

		Location location = e.getClickedBlock().getLocation();

		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			player.setMetadata(SchematicMetadata.SELECTION_POINT_1.name(), new FixedMetadataValue(plugin, location));
			sendSelectedMessage(player, 1, location);
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			player.setMetadata(SchematicMetadata.SELECTION_POINT_2.name(), new FixedMetadataValue(plugin, location));
			sendSelectedMessage(player, 2, location);
		}
	}

	private void sendSelectedMessage(Player player, int point, Location location) {
		player.sendMessage("Point " + point + " selected (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
	}

}
