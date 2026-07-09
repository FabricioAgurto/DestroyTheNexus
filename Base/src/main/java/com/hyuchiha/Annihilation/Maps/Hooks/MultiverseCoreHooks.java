package com.hyuchiha.Annihilation.Maps.Hooks;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class MultiverseCoreHooks implements Hooks {

  private Plugin plugin;

  public MultiverseCoreHooks(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void preUnload(String world, World.Environment environment) {

  }

  @Override
  public void postUnload(String world, World.Environment environment) {

  }

  @Override
  public void preLoad(String world, World.Environment environment) {

  }

  @Override
  public void postLoad(String world, World.Environment environment) {
    // 1.8.8 MVP: avoid compile-time dependency on Multiverse-Core.
    // Multiverse integration can be re-added later through reflection or a dedicated optional module.
    if (plugin.getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
      return;
    }
  }
}
