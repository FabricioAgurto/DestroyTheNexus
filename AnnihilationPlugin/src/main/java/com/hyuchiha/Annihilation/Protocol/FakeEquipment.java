/* The MIT License (MIT)
 *
 * Copyright (c) 2014 Kristian S. Stangeland
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hyuchiha.Annihilation.Protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.NAMED_ENTITY_SPAWN;

/**
 * Modify player equipment.
 *
 * This version is adapted for the legacy Minecraft 1.8.8 equipment packet
 * layout. In 1.8 there is no offhand slot and Bukkit does not have
 * getItemInMainHand()/getItemInOffHand().
 *
 * @author Kristian
 */
public abstract class FakeEquipment {
  /**
   * Represents an equipment slot for Minecraft 1.8.x.
   */
  public enum EquipmentSlot {
    // 1.8 ENTITY_EQUIPMENT packet slots: 0 hand, 1 boots, 2 leggings, 3 chestplate, 4 helmet.
    HAND(0),
    FEET(1),
    LEGS(2),
    CHEST(3),
    HEAD(4);

    private int id;

    private EquipmentSlot(int id) {
      this.id = id;
    }

    /**
     * Retrieve the entity's equipment in the current slot.
     *
     * @param entity - the entity.
     * @return The equipment.
     */
    public ItemStack getEquipment(LivingEntity entity) {
      try {
        switch (this) {
          case HAND:
            return entity.getEquipment().getItemInHand();
          case FEET:
            return entity.getEquipment().getBoots();
          case LEGS:
            return entity.getEquipment().getLeggings();
          case CHEST:
            return entity.getEquipment().getChestplate();
          case HEAD:
            return entity.getEquipment().getHelmet();
          default:
            throw new IllegalArgumentException("Unknown slot: " + this);
        }
      } catch (NullPointerException ex) {
        return null;
      }
    }

    /**
     * Determine if the entity has an equipment in the current slot.
     *
     * @param entity - the entity.
     * @return TRUE if it is empty, FALSE otherwise.
     */
    public boolean isEmpty(LivingEntity entity) {
      ItemStack stack = getEquipment(entity);
      return stack == null || stack.getType() == Material.AIR;
    }

    /**
     * Retrieve the underlying equipment slot ID.
     *
     * @return The ID.
     */
    public int getId() {
      return id;
    }

    /**
     * Find the corresponding equipment slot.
     *
     * @param id - the slot ID.
     * @return The equipment slot.
     */
    public static EquipmentSlot fromId(int id) {
      for (EquipmentSlot slot : values()) {
        if (slot.getId() == id) {
          return slot;
        }
      }
      throw new IllegalArgumentException("Cannot find slot id: " + id);
    }
  }

  /**
   * Represents an equipment event.
   */
  public static class EquipmentSendingEvent {
    private Player client;
    private LivingEntity visibleEntity;
    private EquipmentSlot slot;
    private ItemStack equipment;

    private EquipmentSendingEvent(Player client, LivingEntity visibleEntity, EquipmentSlot slot, ItemStack equipment) {
      this.client = client;
      this.visibleEntity = visibleEntity;
      this.slot = slot;
      this.equipment = equipment;
    }

    public Player getClient() {
      return client;
    }

    public LivingEntity getVisibleEntity() {
      return visibleEntity;
    }

    public ItemStack getEquipment() {
      return equipment;
    }

    public void setEquipment(ItemStack equipment) {
      this.equipment = equipment;
    }

    public EquipmentSlot getSlot() {
      return slot;
    }

    public void setSlot(EquipmentSlot slot) {
      this.slot = Preconditions.checkNotNull(slot, "slot cannot be NULL");
    }
  }

  // Necessary to detect duplicate packets.
  private Map<Object, EquipmentSlot> processedPackets = new MapMaker().weakKeys().makeMap();

  private Plugin plugin;
  private ProtocolManager manager;

  // Current listener.
  private PacketListener listener;

  public FakeEquipment(Plugin plugin) {
    this.plugin = plugin;
    this.manager = ProtocolLibrary.getProtocolManager();

    manager.addPacketListener(
        listener = new PacketAdapter(plugin, ENTITY_EQUIPMENT, NAMED_ENTITY_SPAWN) {
          @Override
          public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            PacketType type = event.getPacketType();

            if (packet.getEntityModifier(event).read(0) instanceof LivingEntity) {
              LivingEntity visibleEntity = (LivingEntity) packet.getEntityModifier(event).read(0);
              Player observingPlayer = event.getPlayer();

              if (ENTITY_EQUIPMENT.equals(type)) {
                EquipmentSlot slot = EquipmentSlot.fromId(packet.getIntegers().read(1));
                ItemStack equipment = packet.getItemModifier().read(0);
                EquipmentSendingEvent sendingEvent = new EquipmentSendingEvent(
                    observingPlayer, visibleEntity, slot, equipment);

                EquipmentSlot previous = processedPackets.get(packet.getHandle());

                if (previous != null) {
                  packet = event.getPacket().deepClone();
                  sendingEvent.setSlot(previous);

                  ItemStack previousEquipment = previous.getEquipment(visibleEntity);
                  sendingEvent.setEquipment(previousEquipment != null ? previousEquipment.clone() : null);
                }

                if (onEquipmentSending(sendingEvent)) {
                  processedPackets.put(packet.getHandle(), previous != null ? previous : slot);
                }

                if (slot != sendingEvent.getSlot()) {
                  packet.getIntegers().write(1, sendingEvent.getSlot().getId());
                }
                if (equipment != sendingEvent.getEquipment()) {
                  packet.getItemModifier().write(0, sendingEvent.getEquipment());
                }

              } else if (NAMED_ENTITY_SPAWN.equals(type)) {
                onEntitySpawn(observingPlayer, visibleEntity);
              } else {
                throw new IllegalArgumentException("Unknown packet type:" + type);
              }
            }
          }
        });
  }

  /**
   * Invoked when a living entity has been spawned on the given client.
   *
   * @param client        - the client.
   * @param visibleEntity - the visibleEntity.
   */
  protected void onEntitySpawn(Player client, LivingEntity visibleEntity) {
    // Update all the slots?
  }

  /**
   * Invoked when the equipment or held item of a living entity is sent to a client.
   *
   * @param equipmentEvent - the equipment event.
   * @return TRUE if the equipment was modified, FALSE otherwise.
   */
  protected abstract boolean onEquipmentSending(EquipmentSendingEvent equipmentEvent);

  /**
   * Update the given slot.
   *
   * @param client        - the observing client.
   * @param visibleEntity - the visible entity that will be updated.
   * @param slot          - the equipment slot to update.
   */
  public void updateSlot(final Player client, LivingEntity visibleEntity, EquipmentSlot slot) {
    if (listener == null) {
      throw new IllegalStateException("FakeEquipment has closed.");
    }

    final PacketContainer equipmentPacket = new PacketContainer(ENTITY_EQUIPMENT);
    equipmentPacket.getIntegers()
        .write(0, visibleEntity.getEntityId())
        .write(1, slot.getId());
    equipmentPacket.getItemModifier()
        .write(0, slot.getEquipment(visibleEntity));

    // We have to send the packet AFTER named entity spawn has been sent.
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        try {
          ProtocolLibrary.getProtocolManager().sendServerPacket(client, equipmentPacket);
        } catch (InvocationTargetException e) {
          throw new RuntimeException("Unable to update slot.", e);
        }
      }
    });
  }

  /**
   * Close the current equipment modifier.
   */
  public void close() {
    if (listener != null) {
      manager.removePacketListener(listener);
      listener = null;
    }
  }
}
