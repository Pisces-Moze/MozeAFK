package com.xbaimiao.minecraft.exppond.listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.xbaimiao.minecraft.exppond.pond.PondManager;

public class PondPlayerListener implements Listener {
    private final Map<UUID, String> playerPond = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerNextTrigger = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastSubtitle = new ConcurrentHashMap<>();

    public PondPlayerListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PondManager.Pond pond = PondManager.getPondByPlayer(player);
                    if (pond != null) {
                        long now = System.currentTimeMillis();
                        long next = playerNextTrigger.getOrDefault(player.getUniqueId(), 0L);
                        if (now >= next) {
                            triggerCommands(player, pond);
                            long minCooldown = getMinCooldown(pond);
                            playerNextTrigger.put(player.getUniqueId(), now + minCooldown * 1000);
                        }
                        long remain = Math.max(0, (playerNextTrigger.getOrDefault(player.getUniqueId(), 0L) - now) / 1000);
                        sendTitle(player, pond, remain);
                        playerPond.put(player.getUniqueId(), pond.world);
                    } else {
                        if (playerPond.containsKey(player.getUniqueId())) {
                            player.sendTitle("", "", 0, 1, 0);
                            playerPond.remove(player.getUniqueId());
                            playerNextTrigger.remove(player.getUniqueId());
                            lastSubtitle.remove(player.getUniqueId());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(com.xbaimiao.minecraft.exppond.Main.getInstance(), 20, 20);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // 仅用于实时检测，主逻辑在定时器
    }

    private void triggerCommands(Player player, PondManager.Pond pond) {
        if (pond.commands == null) return;
        for (Map<String, Object> cmd : pond.commands) {
            String command = (String) cmd.getOrDefault("cmd", "");
            double chance = 1.0;
            try {
                chance = Double.parseDouble(cmd.getOrDefault("chance", 1.0).toString());
            } catch (Exception ignored) {}
            if (Math.random() <= chance) {
                // 只对池内玩家有效
                if (PondManager.getPondByPlayer(player) == pond) {
                    Bukkit.getScheduler().runTask(com.xbaimiao.minecraft.exppond.Main.getInstance(),
                        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()))
                    );
                }
            }
        }
    }

    private long getMinCooldown(PondManager.Pond pond) {
        return pond.cooldown > 0 ? pond.cooldown : 10;
    }

    private void sendTitle(Player player, PondManager.Pond pond, long remain) {
        String subtitle = pond.subtitle != null ? pond.subtitle : "&e挂机池奖励倒计时: &a{time}s";
        String subtitleReplaced = subtitle.replace("{time}", String.valueOf(remain));
        subtitleReplaced = ChatColor.translateAlternateColorCodes('&', subtitleReplaced);
        String last = lastSubtitle.get(player.getUniqueId());
        // 只有内容变化时才刷新，防止闪烁
        if (!subtitleReplaced.equals(last)) {
            player.sendTitle(pond.title != null ? ChatColor.translateAlternateColorCodes('&', pond.title) : "", subtitleReplaced, 0, 40, 0);
            lastSubtitle.put(player.getUniqueId(), subtitleReplaced);
        }
    }
}
