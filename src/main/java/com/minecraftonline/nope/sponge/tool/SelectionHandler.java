package com.minecraftonline.nope.sponge.tool;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.math.Vector3i;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.key.NopeKeys;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

  public ItemStack boxTool() {
    ItemStack itemStack = ItemStack.builder()
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
    itemStack.offer(NopeKeys.SELECTION_TOOL_CUBOID, true);
    return itemStack;
  }

  public boolean isBoxTool(ItemStack stack) {
    return stack.get(NopeKeys.SELECTION_TOOL_CUBOID).isPresent();
  }

  public ItemStack cylinderTool() {
    ItemStack itemStack = ItemStack.builder()
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
        Formatter.accent("___ is the distance between ___ and ___ positions in x-z plane",
            "Radius", "first", "second"),
        Formatter.accent("___ is the distance between ___ and ___ positions in y axis",
            "Height", "first", "second")));
    itemStack.offer(NopeKeys.SELECTION_TOOL_CYLINDER, true);
    return itemStack;
  }

  public boolean isCylinderTool(ItemStack stack) {
    return stack.get(NopeKeys.SELECTION_TOOL_CYLINDER).isPresent();
  }

  public ItemStack sphereTool() {
    ItemStack itemStack = ItemStack.builder()
        .itemType(ItemTypes.STICK)
        .build();
    itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
        .append(Component.text("Sphere").color(Formatter.ACCENT)));
    itemStack.offer(Keys.LORE, Lists.newArrayList(
        Formatter.accent("A tool to select a ___",
            "sphere"),
        Formatter.accent("___ to select the ___ position", "Left-click", "first"),
        Formatter.accent("___ to select the ___ position", "Right-click", "second"),
        Formatter.accent("___ is the distance between ___ and ___ positions in y axis", "Height", "first", "second")));
    itemStack.offer(NopeKeys.SELECTION_TOOL_SPHERE, true);
    return itemStack;
  }

  public boolean isSphereTool(ItemStack stack) {
    return stack.get(NopeKeys.SELECTION_TOOL_SPHERE).isPresent();
  }

  public ItemStack slabTool() {
    ItemStack itemStack = ItemStack.builder()
        .itemType(ItemTypes.STICK)
        .build();
    itemStack.offer(Keys.CUSTOM_NAME, Component.text("Nope Tool: ").color(Formatter.THEME)
        .append(Component.text("Slab").color(Formatter.ACCENT)));
    itemStack.offer(Keys.LORE, Lists.newArrayList(
        Formatter.accent("A tool to select a ___",
            "slab"),
        Formatter.accent("___ to select the ___ position", "Left-click", "first"),
        Formatter.accent("___ to select the ___ position", "Right-click", "second")));
    itemStack.offer(NopeKeys.SELECTION_TOOL_SLAB, true);
    return itemStack;
  }

  public boolean isSlabTool(ItemStack stack) {
    return stack.get(NopeKeys.SELECTION_TOOL_SLAB).isPresent();
  }

  @Listener(order = Order.FIRST)
  public void onSelect(InteractBlockEvent.Primary.Start event) {
    Optional<ServerPlayer> playerOptional = event.cause().first(ServerPlayer.class);
    if (!playerOptional.isPresent()) {
      return;
    }

    ServerPlayer player = playerOptional.get();
    Optional<ServerLocation> locationOptional = event.block().location();
    if (!locationOptional.isPresent()) {
      return;
    }
    ServerLocation location = locationOptional.get();

    ItemStack itemStack = player.itemInHand(HandTypes.MAIN_HAND);
    if (isBoxTool(itemStack)) {
      updateSelection(player,
          boxDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CuboidSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          true);
      event.setCancelled(true);
    } else if (isCylinderTool(itemStack)) {
      updateSelection(player,
          cylinderDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CylinderSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          true);
      event.setCancelled(true);
    } else if (isSphereTool(itemStack)) {
      updateSelection(player,
          sphereDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SphereSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          true);
      event.setCancelled(true);
    } else if (isSlabTool(itemStack)) {
      updateSelection(player,
          slabDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SlabSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          true);
      event.setCancelled(true);
    }
  }

  @Listener(order = Order.FIRST)
  public void onSelect(InteractBlockEvent.Secondary event) {
    Nope.instance().logger().info("Secondary interact");
    Optional<ServerPlayer> playerOptional = event.cause().first(ServerPlayer.class);
    if (!playerOptional.isPresent()) {
      return;
    }

    ServerPlayer player = playerOptional.get();
    Optional<ServerLocation> locationOptional = event.block().location();
    if (!locationOptional.isPresent()) {
      return;
    }
    ServerLocation location = locationOptional.get();

    ItemStack itemStack = player.itemInHand(HandTypes.MAIN_HAND);
    if (isBoxTool(itemStack)) {
      updateSelection(player,
          boxDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CuboidSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          false);
      event.setCancelled(true);
    } else if (isCylinderTool(itemStack)) {
      updateSelection(player,
          cylinderDrafts.computeIfAbsent(player.uniqueId(), uuid -> new CylinderSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          false);
      event.setCancelled(true);
    } else if (isSphereTool(itemStack)) {
      updateSelection(player,
          sphereDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SphereSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          false);
      event.setCancelled(true);
    } else if (isSlabTool(itemStack)) {
      updateSelection(player,
          slabDrafts.computeIfAbsent(player.uniqueId(), uuid -> new SlabSelection()),
          SpongeNope.instance().hostSystem().domain(SpongeUtil.worldToId(location.world())),
          Vector3i.of(location.blockX(), location.blockY(), location.blockZ()),
          false);
      event.setCancelled(true);
    }
  }

  private void updateSelection(ServerPlayer player,
                               Selection<?> selection, Domain domain, Vector3i position,
                               boolean first) {
    final Domain originalDomain = selection.domain();

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
        selection.domain().id()
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

}
