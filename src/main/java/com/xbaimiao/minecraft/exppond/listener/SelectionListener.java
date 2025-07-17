package com.xbaimiao.minecraft.exppond.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.xbaimiao.minecraft.exppond.selection.SelectionManager;

public class SelectionListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.GOLDEN_AXE) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().contains("挂机池选区斧")) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            SelectionManager.setPoint(player, 1, block.getLocation());
            player.sendMessage(ChatColor.GREEN + "已设置A点: " + block.getX() + "," + block.getY() + "," + block.getZ());
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            SelectionManager.setPoint(player, 2, block.getLocation());
            player.sendMessage(ChatColor.GREEN + "已设置B点: " + block.getX() + "," + block.getY() + "," + block.getZ());
            event.setCancelled(true);
        }
    }
}
