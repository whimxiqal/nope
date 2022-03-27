/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.tool;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.math.Vector3i;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.key.NopeKeys;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * A manager for all users' in-game {@link me.pietelite.nope.common.math.Volume}
 * {@link Selection}s.
 */
public class SelectionHandler {

  private final Map<UUID, CuboidSelection> boxDrafts = new HashMap<>();
  private final Map<UUID, CylinderSelection> cylinderDrafts = new HashMap<>();
  private final Map<UUID, SphereSelection> sphereDrafts = new HashMap<>();
  private final Map<UUID, SlabSelection> slabDrafts = new HashMap<>();

  public CuboidSelection boxDraft(UUID playerUuid) {
    return boxDrafts.get(playerUuid);
  }

  public CylinderSelection cylinderDraft(UUID playerUuid) {
    return cylinderDrafts.get(playerUuid);
  }

  public SphereSelection sphereDraft(UUID playerUuid) {
    return sphereDrafts.get(playerUuid);
  }

  public SlabSelection slabDraft(UUID playerUuid) {
    return slabDrafts.get(playerUuid);
  }

  /**
   * Check if this item stack is a selection tool or not.
   *
   * @param stack the stack
   * @return true if it is a tool
   */
  public boolean isTool(ValueContainer stack) {
    return toolType(stack) != null;
  }


  /**
   * Check if this stack is a specific type of tool.
   *
   * @param stack the stack
   * @param type  the type of selection
   * @return true if the stack is the requested type of tool
   */
  public boolean isTool(ValueContainer stack, Selection.Type type) {
    switch (type) {
      case CUBOID:
        return stack.get(NopeKeys.SELECTION_TOOL_CUBOID).isPresent();
      case CYLINDER:
        return stack.get(NopeKeys.SELECTION_TOOL_CYLINDER).isPresent();
      case SLAB:
        return stack.get(NopeKeys.SELECTION_TOOL_SLAB).isPresent();
      case SPHERE:
        return stack.get(NopeKeys.SELECTION_TOOL_SPHERE).isPresent();
      default:
        return false;
    }
  }

  /**
   * Get the type of tool this container is.
   *
   * @param stack the item stack
   * @return the type, or null if none
   */
  @Nullable
  public Selection.Type toolType(ValueContainer stack) {
    for (Selection.Type type : Selection.Type.values()) {
      if (isTool(stack, type)) {
        return type;
      }
    }
    return null;
  }

  /**
   * Create a new tool of the requested type.
   *
   * @param type the type
   * @return the item stack that may be used in-game as a tool
   */
  public ItemStack createTool(Selection.Type type) {
    ItemStack itemStack;
    switch (type) {
      case CUBOID:
        itemStack = ItemStack.builder()
            .itemType(ItemTypes.STICK)
            .build();
        itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
            .append(Component.text("Box").color(Formatter.ACCENT)));
        itemStack.offer(Keys.LORE, Lists.newArrayList(
            Formatter.accent("A tool to select a ___",
                "box"),
            Formatter.accent("___ to select the ___ position", "Left-click", "first"),
            Formatter.accent("___ to select the ___ position", "Right-click", "second"),
            Formatter.accent("These will be the corners of the box")));
        Formatter.dull("Sneak and left- or right-click to rotate the type of tool");
        itemStack.offer(NopeKeys.SELECTION_TOOL_CUBOID, true);
        return itemStack;
      case CYLINDER:
        itemStack = ItemStack.builder()
            .itemType(ItemTypes.STICK)
            .build();
        itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
            .append(Component.text("Cylinder").color(Formatter.ACCENT)));
        itemStack.offer(Keys.LORE, Lists.newArrayList(
            Formatter.accent("A tool to select a ___",
                "cylinder"),
            Formatter.accent("___ to select the ___ position", "Left-click", "first"),
            Formatter.accent("___ to select the ___ position", "Right-click", "second"),
            Formatter.accent("___ position defines the ___", "First", "center"),
            Formatter.accent("___ is the distance between ___ and", "Radius", "first"),
            Formatter.accent("   ___ positions in the x-z plane", "second"),
            Formatter.accent("___ is the distance between ___ and ", "Height", "first"),
            Formatter.accent("   ___ positions on the y axis", "second")));
        itemStack.offer(NopeKeys.SELECTION_TOOL_CYLINDER, true);
        return itemStack;
      case SPHERE:
        itemStack = ItemStack.builder()
            .itemType(ItemTypes.STICK)
            .build();
        itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
            .append(Component.text("Sphere").color(Formatter.ACCENT)));
        itemStack.offer(Keys.LORE, Lists.newArrayList(
            Formatter.accent("A tool to select a ___",
                "sphere"),
            Formatter.accent("___ to select the ___ position", "Left-click", "first"),
            Formatter.accent("___ to select the ___ position", "Right-click", "second"),
            Formatter.accent("___ position defines the ___", "First", "center"),
            Formatter.accent("___ is the distance between ___ and", "Radius", "first"),
            Formatter.accent("   ___ positions", "second")));
        itemStack.offer(NopeKeys.SELECTION_TOOL_SPHERE, true);
        return itemStack;
      case SLAB:
        itemStack = ItemStack.builder()
            .itemType(ItemTypes.STICK)
            .build();
        itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
            .append(Component.text("Slab").color(Formatter.ACCENT)));
        itemStack.offer(Keys.LORE, Lists.newArrayList(
            Formatter.accent("A tool to select a ___",
                "slab"),
            Formatter.accent("___ to select the ___ y value", "Left-click", "first"),
            Formatter.accent("___ to select the ___ y value", "Right-click", "second")));
        itemStack.offer(NopeKeys.SELECTION_TOOL_SLAB, true);
        return itemStack;
      default:
        throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  /**
   * Event handler for selecting the first position in {@link Selection}s
   * or rotating between tools in-hand.
   *
   * @param event the event
   */
  @Listener(order = Order.FIRST)
  public void onSelect(InteractBlockEvent.Primary.Start event) {
    onSelect(event, true);
  }

  /**
   * Event handler for selecting the second position in {@link Selection}s
   * or rotating between tools in-hand.
   *
   * @param event the event
   */
  @Listener(order = Order.FIRST)
  public void onSelect(InteractBlockEvent.Secondary event) {
    Optional<HandType> handType = event.context().get(EventContextKeys.USED_HAND);
    if (handType.isPresent() && handType.get().equals(HandTypes.MAIN_HAND.get())) {
      onSelect(event, false);
    }
  }

  private <E extends InteractBlockEvent & Cancellable> void onSelect(E event,
                                                                     boolean first) {
    Optional<ServerPlayer> playerOptional = event.cause().first(ServerPlayer.class);
    if (!playerOptional.isPresent()) {
      return;
    }
    ServerPlayer player = playerOptional.get();
    ItemStack itemStack = player.itemInHand(HandTypes.MAIN_HAND);
    Selection.Type toolType = toolType(itemStack);
    if (toolType == null) {
      return;
    }
    if (player.sneaking().get()) {
      if (first) {
        switch (toolType) {
          case CUBOID:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.CYLINDER));
            break;
          case CYLINDER:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.SPHERE));
            break;
          case SPHERE:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.SLAB));
            break;
          case SLAB:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.CUBOID));
            break;
          default:
        }
      } else {
        switch (toolType) {
          case CUBOID:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.SLAB));
            break;
          case CYLINDER:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.CUBOID));
            break;
          case SPHERE:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.CYLINDER));
            break;
          case SLAB:
            player.setItemInHand(HandTypes.MAIN_HAND, createTool(Selection.Type.SPHERE));
            break;
          default:
        }
      }
    } else {
      Optional<ServerLocation> locationOptional = event.block().location();
      if (!locationOptional.isPresent()) {
        return;
      }
      ServerLocation location = locationOptional.get();
      switch (toolType) {
        case CUBOID:
          updateSelection(player,
              boxDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CuboidSelection()),
              SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
              Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
              first);
          break;
        case CYLINDER:
          updateSelection(player,
              cylinderDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CylinderSelection()),
              SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
              Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
              first);
          break;
        case SPHERE:
          updateSelection(player,
              sphereDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SphereSelection()),
              SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
              Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
              first);
          break;
        case SLAB:
          updateSelection(player,
              slabDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SlabSelection()),
              SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
              Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
              first);
          break;
        default:
      }
    }
    event.setCancelled(true);

  }

  private void updateSelection(ServerPlayer player,
                               Selection<?> selection, Domain domain, Vector3i position,
                               boolean first) {
    final Domain originalDomain = selection.domain();

    if (Objects.equals(selection.domain(), domain)
        && ((first && Objects.equals(selection.position1, position))
        || (!first && Objects.equals(selection.position2, position)))) {
      player.sendMessage(Formatter.success(
          "Position ___ is already set there",
          first ? 1 : 2
      ));
      return;
    }

    selection.domain(domain);
    if (first) {
      selection.position1(position);
    } else {
      selection.position2(position);
    }
    player.sendMessage(Formatter.success(
        "Set position ___ to ___, ___, ___ in ___",
        first ? 1 : 2,
        position.x(),
        position.y(),
        position.z(),
        selection.domain().name()
    ));

    if (originalDomain != null && originalDomain != domain) {
      player.sendMessage(Formatter.warn("Position ___ has been erased because "
              + "your new position ___ is in a different world",
          first ? 2 : 1,
          first ? 1 : 2));
      if (first) {
        selection.position2(null);
      } else {
        selection.position1(null);
      }
    }

    if (selection.isComplete()) {
      selection.errors().forEach(error -> player.sendMessage(Formatter.error(error)));
      if (selection.validate()) {
        player.sendMessage(selection.description());
      }
    }
  }

  /**
   * Event handler to ensure that tools are not misplaced by removing them if dropped.
   *
   * @param event the event
   */
  @Listener(order = Order.FIRST)
  public void onDrop(SpawnEntityEvent event) {
    for (Entity entity : event.entities()) {
      if (entity.isRemoved()) {
        continue;
      }
      if (entity instanceof Item) {
        Item item = (Item) entity;
        if (isTool(item.item().get())) {
          item.remove();
        }
      }
    }
  }

}
