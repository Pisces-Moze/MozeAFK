package com.xbaimiao.minecraft.exppond;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        // 彩蛋LOGO和作者信息
        getLogger().info(" _____ ______       ________      ________      _______      ");
        getLogger().info("|\\   _ \\  _   \\    |\\   __  \\    |\\_____  \\    |\\  ___ \\     ");
        getLogger().info("\\ \\  \\\\__\\ \\  \\   \\ \\  \\|\\  \\    \\|___/  /|   \\ \\   __/|    ");
        getLogger().info(" \\ \\  \\\\|__| \\  \\   \\ \\  \\\\  \\       /  / /    \\ \\  \\_|/__  ");
        getLogger().info("  \\ \\  \\    \\ \\  \\   \\ \\  \\\\  \\     /  /_/__    \\ \\  \\_|\\ \\ ");
        getLogger().info("   \\ \\__\\    \\ \\__\\   \\ \\_______\\   |\\________\\   \\ \\_______\\");
        getLogger().info("    \\|__|     \\|__|    \\|_______|    \\|_______|    \\|_______|");
        getLogger().info("MozeAfk 多区挂机池  By Moze");
        // 注册命令
        if (this.getCommand("mozeafk") != null) {
            this.getCommand("mozeafk").setExecutor(new com.xbaimiao.minecraft.exppond.command.ExppondCommand());
        } else {
            getLogger().warning("未能注册 mozeafk 命令，请检查 plugin.yml");
        }
        // 注册监听器
        getServer().getPluginManager().registerEvents(new com.xbaimiao.minecraft.exppond.listener.SelectionListener(), this);
        getServer().getPluginManager().registerEvents(new com.xbaimiao.minecraft.exppond.listener.PondPlayerListener(), this);
        // 加载所有挂机池配置
        com.xbaimiao.minecraft.exppond.pond.PondManager.loadAll();
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // TODO: 插件关闭时的清理操作
    }
}
