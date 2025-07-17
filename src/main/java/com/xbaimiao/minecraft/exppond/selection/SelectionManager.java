package com.xbaimiao.minecraft.exppond.selection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SelectionManager {
    public static final NamespacedKey WAND_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(SelectionManager.class), "mozeafk_wand");
    private static final Map<Player, Selection> selectionMap = new HashMap<>();

    public static void setPoint(Player player, int point, Location location) {
        Selection sel = selectionMap.getOrDefault(player, new Selection());
        if (point == 1) sel.a = location.clone();
        if (point == 2) sel.b = location.clone();
        selectionMap.put(player, sel);
    }

    public static boolean hasSelection(Player player) {
        Selection sel = selectionMap.get(player);
        return sel != null && sel.a != null && sel.b != null;
    }

    public static boolean saveSelection(Player player, String name) {
        Selection sel = selectionMap.get(player);
        if (sel == null || sel.a == null || sel.b == null) return false;
        File dir = new File(JavaPlugin.getProvidingPlugin(SelectionManager.class).getDataFolder(), "ponds");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, name + ".yml");
        try (FileWriter writer = new FileWriter(file)) {
            String worldName = (sel.a != null && sel.a.getWorld() != null) ? sel.a.getWorld().getName() : (sel.b != null && sel.b.getWorld() != null ? sel.b.getWorld().getName() : "world");
            writer.write("world: " + worldName + "\n");
            writer.write("a: " + sel.a.getBlockX() + "," + sel.a.getBlockY() + "," + sel.a.getBlockZ() + "\n");
            writer.write("b: " + sel.b.getBlockX() + "," + sel.b.getBlockY() + "," + sel.b.getBlockZ() + "\n");
            // 预留命令、冷却等配置
            writer.write("commands:\n  - cmd: 'say 玩家获得奖励'\n    chance: 1.0\n    cooldown: 10\n");
            writer.write("title: '&a挂机池'\n");
            return true;
        } catch (IOException e) {
            System.err.println("保存挂机池选区失败: " + e.getMessage());
            return false;
        }
    }

    public static class Selection {
        public Location a;
        public Location b;
    }
}
