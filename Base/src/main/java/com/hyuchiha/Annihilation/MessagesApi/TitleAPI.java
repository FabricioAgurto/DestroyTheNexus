package com.hyuchiha.Annihilation.MessagesApi;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 1.8.8-safe title fallback.
 *
 * The original implementation depended on reflectionhelper, which is pulled from
 * legacy HTTP Maven repositories blocked by modern Maven. For the 1.8.8 MVP we
 * keep the public API and avoid external dependencies. Visual title packets can
 * be restored later with a dedicated v1_8_R3 packet implementation.
 */
public class TitleAPI {

  public static void send(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    sendTitle(player, title);
    sendSubTitle(player, subtitle);
  }

  public static void sendToAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      send(player, title, subtitle, fadeIn, stay, fadeOut);
    }
  }

  public static void sendTitle(Player player, String jsonOrText) {
    sendFallbackMessage(player, jsonOrText);
  }

  public static void sendTitle(Player player, BaseComponent baseComponent) {
    if (baseComponent != null) {
      sendFallbackMessage(player, baseComponent.toLegacyText());
    }
  }

  public static void sendTitle(Player player, BaseComponent baseComponent, int fadeIn, int stay, int fadeOut) {
    sendTitle(player, baseComponent);
  }

  public static void sendSubTitle(Player player, String jsonOrText) {
    sendFallbackMessage(player, jsonOrText);
  }

  public static void sendSubTitle(Player player, BaseComponent baseComponent) {
    if (baseComponent != null) {
      sendFallbackMessage(player, baseComponent.toLegacyText());
    }
  }

  public static void sendSubTitle(Player player, BaseComponent baseComponent, int fadeIn, int stay, int fadeOut) {
    sendSubTitle(player, baseComponent);
  }

  public static void sendTimings(Player player, int fadeIn, int stay, int fadeOut) {
    // No-op in the MVP fallback.
  }

  public static void clear(Player player) {
    // No-op in the MVP fallback.
  }

  public static void reset(Player player) {
    // No-op in the MVP fallback.
  }

  private static void sendFallbackMessage(Player player, String message) {
    if (player == null || message == null || message.trim().isEmpty()) {
      return;
    }

    player.sendMessage(stripSimpleJson(message));
  }

  private static String stripSimpleJson(String message) {
    if (message.startsWith("{\"text\": \"") && message.endsWith("\"}")) {
      return message.substring(10, message.length() - 2);
    }

    return message;
  }
}
