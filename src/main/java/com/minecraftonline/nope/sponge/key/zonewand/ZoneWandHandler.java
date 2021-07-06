/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.sponge.key.zonewand;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.sponge.util.Format;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A handler for players using the zone wand.
 */
public class ZoneWandHandler {
  private final Map<UUID, Selection> selectionMap = new HashMap<>();

  public Map<UUID, Selection> getSelectionMap() {
    return selectionMap;
  }

  /**
   * Event handler for interacting with blocks.
   *
   * @param event the event
   */
  @Listener(order = Order.FIRST)
  public void interactBlockEvent(InteractBlockEvent event) {
    if (handleEvent(event, event.getTargetBlock())) {
      event.setCancelled(true);
    }
  }

  private boolean handleEvent(Event event, BlockSnapshot block) {
    if (!block.getLocation().isPresent()) {
      return false; // click in the air
    }
    MutableBoolean mutableBoolean = new MutableBoolean(false);
    event.getCause().first(Player.class).ifPresent(player ->
        player.getItemInHand(HandTypes.MAIN_HAND)
            .filter(this::isWand)
            .ifPresent(wand -> {
              if (!player.hasPermission(Permissions.COMMAND_CREATE.get())) {
                player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
                player.sendMessage(Format.error("You don't have permission to use this!"));
                return;
              }
              mutableBoolean.setTrue();
              selectionMap.compute(player.getUniqueId(), (k, v) -> {
                if (v == null) {
                  v = new Selection();
                }
                if (event instanceof InteractBlockEvent.Primary) {
                  v.setPos1(block.getLocation().get(), player); // left click
                } else if (event instanceof InteractBlockEvent.Secondary) {
                  v.setPos2(block.getLocation().get(), player); // right click
                }
                return v;
              });
            }));
    return mutableBoolean.booleanValue();
  }

  private boolean isWand(ItemStack itemStack) {
    ItemStack wandItemStack = ItemStack.builder()
        .itemType(Sponge.getRegistry()
            .getType(ItemType.class, SpongeNope.getInstance()
                .getHostTreeAdapter()
                .getGlobalHost()
                .get(SettingLibrary.WAND_ITEM)
                .map(SettingValue::getData)
                .orElse(SettingLibrary.WAND_ITEM.getDefaultData()))
            .orElseThrow(() -> new IllegalStateException("Storing an illegal wand id")))
        .quantity(1)
        .build();
    if (!wandItemStack.getType().equals(itemStack.getType())) {
      return false;
    }
    return itemStack.get(ZoneWandManipulator.class)
        .map(ZoneWandManipulator::isWand)
        .map(Value::get)
        .orElse(false);
  }

  /**
   * A prismatic Nope-specific selection.
   */
  public static class Selection {
    @Getter
    @Nullable
    private World world = null;
    @Nullable
    private Vector3i pos1 = null;
    @Nullable
    private Vector3i pos2 = null;

    public Selection() {
    }

    /**
     * Default constructor.
     *
     * @param world the world of the selection
     * @param pos1 the position of the first corner
     * @param pos2 the position of the second corner
     */
    public Selection(@Nullable World world,
                     @Nullable Vector3i pos1,
                     @Nullable Vector3i pos2) {
      this.world = world;
      this.pos1 = pos1;
      this.pos2 = pos2;
    }

    /**
     * Sets the first position of the selection.
     *
     * @param location the location for the position
     * @param src the source requesting to set the position
     */
    public void setPos1(Location<World> location, CommandSource src) {
      if (this.world != null && !this.world.equals(location.getExtent())) {
        this.pos2 = null;
        src.sendMessage(Format.info("Your selection has changed worlds, your other position was removed"));
      }
      // Position changed?
      if (!(location.getExtent().equals(this.world) && location.getBlockPosition().equals(this.pos1))) {
        this.world = location.getExtent();
        this.pos1 = location.getBlockPosition();
        src.sendMessage(Format.success("Position 1 set to ", Format.ACCENT, this.pos1));
      }
    }

    /**
     * Sets the second position of the selection.
     *
     * @param location the location for the position
     * @param src the source requesting to set the position
     */
    public void setPos2(Location<World> location, CommandSource src) {
      if (this.world != null && !this.world.equals(location.getExtent())) {
        this.pos1 = null;
        src.sendMessage(Format.info("Your selection has changed worlds, your other position was removed"));
      }
      // Position changed?
      if (!(location.getExtent().equals(this.world) && location.getBlockPosition().equals(this.pos2))) {
        this.world = location.getExtent();
        this.pos2 = location.getBlockPosition();
        src.sendMessage(Format.success("Position 2 set to ", Format.ACCENT, this.pos2));
      }
    }

    public boolean isComplete() {
      return this.pos1 != null
          && this.pos2 != null;
    }

    /**
     * Get the minimum coordinate set.
     *
     * @return the coordinates
     */
    @Nullable
    public Vector3i getMin() {
      return pos1 == null
          ? (pos2 == null ? null : pos2)
          : (pos2 == null ? pos1 : pos1.min(pos2));
    }

    /**
     * Get the maximum coordinate set.
     *
     * @return the coordinates
     */
    @Nullable
    public Vector3i getMax() {
      return pos1 == null
          ? (pos2 == null ? null : pos2)
          : (pos2 == null ? pos1 : pos1.max(pos2));
    }

  }
}
