package com.xbaimiao.minecraft.exppond.pond;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PondManager {
    private static final Map<String, Pond> ponds = new HashMap<>();

    public static void loadAll() {
        ponds.clear();
        File dir = new File(JavaPlugin.getProvidingPlugin(PondManager.class).getDataFolder(), "ponds");
        if (!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            String world = yml.getString("world");
            String aStr = yml.getString("a");
            String bStr = yml.getString("b");
            String[] a = (aStr != null) ? aStr.split(",") : null;
            String[] b = (bStr != null) ? bStr.split(",") : null;
            List<Map<String, Object>> commands = null;
            try {
                Object listObj = yml.getList("commands");
                if (listObj instanceof List<?>) {
                    commands = new ArrayList<>();
                    for (Object o : (List<?>) listObj) {
                        if (o instanceof Map) {
                            commands.add((Map<String, Object>) o);
                        }
                    }
                }
            } catch (Exception ignored) {}
            long cooldown = yml.getLong("cooldown", 10);
            String subtitle = yml.getString("subtitle", "&e挂机池奖励倒计时: &a{time}s");
            String title = yml.getString("title", "&a挂机池");
            if (world == null || a == null || b == null || a.length != 3 || b.length != 3) continue;
            try {
                ponds.put(file.getName().replace(".yml", ""), new Pond(
                        world,
                        new int[]{Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2])},
                        new int[]{Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2])},
                        commands,
                        title,
                        subtitle,
                        cooldown
                ));
            } catch (Exception e) {
                // 忽略格式错误的池
            }
        }
    }

    public static Collection<Pond> getAll() {
        return ponds.values();
    }

    public static Pond getPond(String name) {
        return ponds.get(name);
    }

    public static Pond getPondByPlayer(Player player) {
        Location loc = player.getLocation();
        for (Pond pond : ponds.values()) {
            if (pond.isIn(loc)) return pond;
        }
        return null;
    }

    public static class Pond {
        public final String world;
        public final int[] a;
        public final int[] b;
        public final List<Map<String, Object>> commands;
        public final String title;
        public final String subtitle;
        public final long cooldown;
        private final int minX, maxX, minY, maxY, minZ, maxZ;

        public Pond(String world, int[] a, int[] b, List<Map<String, Object>> commands, String title, String subtitle, long cooldown) {
            this.world = world;
            this.a = a;
            this.b = b;
            this.commands = commands == null ? new ArrayList<>() : commands;
            this.title = title;
            this.subtitle = subtitle;
            this.cooldown = cooldown;
            this.minX = Math.min(a[0], b[0]);
            this.maxX = Math.max(a[0], b[0]);
            this.minY = Math.min(a[1], b[1]);
            this.maxY = Math.max(a[1], b[1]);
            this.minZ = Math.min(a[2], b[2]);
            this.maxZ = Math.max(a[2], b[2]);
        }

        public boolean isIn(Location loc) {
            if (loc == null) return false;
            if (loc.getWorld() == null) return false;
            if (world == null || !world.equals(loc.getWorld().getName())) return false;
            int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
    }
}
