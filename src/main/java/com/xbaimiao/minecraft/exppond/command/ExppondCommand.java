package com.xbaimiao.minecraft.exppond.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.xbaimiao.minecraft.exppond.selection.SelectionManager;

public class ExppondCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用该命令");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
            ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
            ItemMeta meta = axe.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + "挂机池选区斧");
                meta.setUnbreakable(true);
                meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
                meta.getPersistentDataContainer().set(SelectionManager.WAND_KEY, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
                axe.setItemMeta(meta);
                player.getInventory().addItem(axe);
                player.sendMessage("§a已获得挂机池选区斧，左键/右键设置A/B点");
            } else {
                player.sendMessage("§c无法生成选区斧，请联系管理员");
            }
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            String name = args[1];
            if (!SelectionManager.hasSelection(player)) {
                player.sendMessage("§c请先用选区斧设置A/B点");
                return true;
            }
            boolean success = SelectionManager.saveSelection(player, name);
            if (success) {
                player.sendMessage("§a挂机池 " + name + " 创建成功！");
            } else {
                player.sendMessage("§c创建失败，可能是文件写入错误");
            }
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            com.xbaimiao.minecraft.exppond.pond.PondManager.loadAll();
            player.sendMessage("§a挂机池配置已重载");
            return true;
        }
        player.sendMessage("§e/mozeafk wand §7获得选区斧\n§e/mozeafk create [名字] §7创建挂机池\n§e/mozeafk reload §7重载池配置");
        return true;
    }
}
