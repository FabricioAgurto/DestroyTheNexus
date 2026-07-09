package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_8_R3 extends TileEntityFurnace implements VirtualFurnace {

  private EntityPlayer handle;

  public VirtualFurnace_v1_8_R3(Player player) {
    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
  }

  @Override
  public boolean canCook() {
    return getItem(0) != null && (getItem(1) != null || this.getProperty(0) > 0);
  }

  @Override
  public void cook() {
    this.c();
  }

  /**
   * Keep fuel time permissive for the virtual furnace.
   * Do not annotate with @Override because this NMS method differs between versions.
   */
  public int a(ItemStack itemstack) {
    return 100;
  }

  @Override
  public boolean a(EntityHuman entity) {
    return true;
  }

  @Override
  public InventoryHolder getOwner() {
    return () -> new CraftInventoryFurnace(this);
  }

  @Override
  public void openFurnace() {
    handle.openContainer(this);
  }
}
