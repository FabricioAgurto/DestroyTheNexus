package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_8_R3 extends TileEntityBrewingStand implements VirtualBrewingStand {

  private EntityPlayer handle;

  public VirtualBrewingStand_v1_8_R3(Player player) {
    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
  }

  @Override
  public boolean canMakePotions() {
    return getProperty(0) <= 0
        && getItem(3) != null
        && (getItem(0) != null
            || getItem(1) != null
            || getItem(2) != null);
  }

  @Override
  public void makePotions() {
    this.c();
  }

  @Override
  public boolean a(EntityHuman entity) {
    return true;
  }

  @Override
  public InventoryHolder getOwner() {
    return () -> new CraftInventoryBrewer(this);
  }

  @Override
  public void openBrewingStand() {
    handle.openContainer(this);
  }
}
