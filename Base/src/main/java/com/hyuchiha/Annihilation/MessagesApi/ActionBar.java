package com.hyuchiha.Annihilation.MessagesApi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 1.8.8-safe action bar fallback.
 *
 * The original implementation depended on reflectionhelper. For the MVP we keep
 * the same public API and use chat fallback to avoid external legacy dependencies.
 */
public class ActionBar {

  public static void send(Player player, String message) {
    sendMessage(player, message);
  }

  public static void sendMessage(Player player, String jsonOrText) {
    if (player == null || jsonOrText == null || jsonOrText.trim().isEmpty()) {
      return;
    }

    player.sendMessage(stripSimpleJson(jsonOrText));
  }

  public static void sendToAll(String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      send(player, message);
    }
  }

  private static String stripSimpleJson(String message) {
    if (message.startsWith("{\"text\": \"") && message.endsWith("\"}")) {
      return message.substring(10, message.length() - 2);
    }

    return message;
  }
}
