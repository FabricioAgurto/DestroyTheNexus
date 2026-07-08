package com.hyuchiha.Annihilation.BossBar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarAPI {
  private static final Map<UUID, String> barMessages = new ConcurrentHashMap<>();
  private static Plugin pluginInstance;

  public static void init(Plugin plugin) {
    pluginInstance = plugin;

    for (Player player : Bukkit.getOnlinePlayers()) {
      removeBar(player);
    }

    PluginManager pm = plugin.getServer().getPluginManager();
    pm.registerEvents(new BossBarListener(), plugin);
  }

  /**
   * Legacy-compatible placeholder implementation.
   *
   * Bukkit's native BossBar API does not exist in Minecraft/Spigot 1.8.8.
   * To avoid NoClassDefFoundError on legacy servers, this class intentionally
   * avoids direct references to org.bukkit.boss.*. A visual 1.8 bossbar can be
   * added later using packets or a fake entity implementation.
   */
  public static void setMessage(Player player, String message, float percentage) {
    if (player == null) {
      return;
    }

    barMessages.put(player.getUniqueId(), message);
  }

  public static void removeBar(Player player) {
    if (player == null) {
      return;
    }

    barMessages.remove(player.getUniqueId());
  }

  public static boolean hasBar(Player player) {
    return player != null && barMessages.containsKey(player.getUniqueId());
  }

  public static String getBossBar(Player player) {
    if (player == null) {
      return null;
    }

    return barMessages.get(player.getUniqueId());
  }

  public static void handlePlayerTeleport(Player player) {
    // No visual bossbar is spawned in this 1.8-safe implementation yet.
  }
}
