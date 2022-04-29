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

package me.pietelite.nope.sponge.listener.always;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.pietelite.nope.common.api.register.data.Movement;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.util.TreeUtil;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import me.pietelite.nope.sponge.listener.SettingEventContextImpl;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * The listener containing all handlers for managing movement events.
 */
public class MovementListener {

  Set<EntityType<?>> loggable = Sets.newHashSet(EntityTypes.PLAYER.get(),
      EntityTypes.BOAT.get(),
      EntityTypes.HORSE.get());

  /**
   * Handler for entities moving.
   *
   * @param event the event
   */
  @Listener(order = Order.FIRST)
  public void onMove(MoveEntityEvent event) {
    final ServerWorld world = event.entity().serverLocation().world();
    moving(event, world, world);
  }

  @Listener(order = Order.FIRST)
  public void onChangeWorldReposition(ChangeEntityWorldEvent.Reposition event) {
    moving(event, event.originalWorld(), event.destinationWorld());
  }

  private void moving(MoveEntityEvent event, ServerWorld firstWorld, ServerWorld lastWorld) {
    List<Entity> entities = TreeUtil.getAllInTree(
        event.entity().baseVehicle()
            .map(Value::get).orElse(event.entity()),
        entity -> entity,
        entity -> entity.passengers().all());

    for (Entity entity : entities) {
      /*
      if (entity.type().equals(EntityTypes.HORSE.get())) {
        if (!entity.passengers().all().isEmpty()) {
          Nope.instance().logger().info("Added passengers to " + entity.type() + ": " + entity.passengers()
          .all().stream()
          .map(Object::toString)
          .collect(Collectors.joining(", ")));
        } else {
          Nope.instance().logger().info("Entity " + entity.type() + " has no passengers");
        }
      }
       */

      final Movement movementType = SpongeUtil.reduceMovementType(event.context()
          .get(EventContextKeys.MOVEMENT_TYPE)
          .orElse(MovementTypes.NATURAL.get()));
      final Location firstLocation = new Location(event.originalPosition().x(),
          event.originalPosition().y(),
          event.originalPosition().z(),
          SpongeUtil.reduceWorld(firstWorld));
      final Location lastLocation = new Location(event.destinationPosition().x(),
          event.destinationPosition().y(),
          event.destinationPosition().z(),
          SpongeUtil.reduceWorld(lastWorld));

      // Check for MOVE setting
      if (!SpongeUtil.valueFor(SettingKeys.MOVE, entity, firstLocation).contains(movementType)
          || !SpongeUtil.valueFor(SettingKeys.MOVE, entity, lastLocation).contains(movementType)) {
        cancelMovement(event, movementType, firstWorld, entity, SettingKeys.MOVE);
      }

      // Check for EXIT and ENTRY
      Set<Host> firstHosts = SpongeNope.instance().system().collectSuperiorHosts(firstLocation);
      Set<Host> lastHosts = SpongeNope.instance().system().collectSuperiorHosts(lastLocation);

      // Remove shared hosts
      Set<Host> shared = new HashSet<>(firstHosts);
      shared.retainAll(lastHosts);
      firstHosts.removeAll(shared);
      lastHosts.removeAll(shared);

      // Cannot exit if we are walking out of a host and EXIT is disallowed at the origin
      if (!firstHosts.isEmpty()
          && !SpongeUtil.valueFor(SettingKeys.EXIT, entity, firstLocation).contains(movementType)) {
        cancelMovement(event, movementType, firstWorld, entity, SettingKeys.EXIT);
        if (entity instanceof Audience) {
          trySendMessage((Audience) entity, SpongeUtil.valueFor(SettingKeys.EXIT_DENY_MESSAGE,
              entity,
              lastLocation));
          trySendTitle((Audience) entity, SpongeUtil.valueFor(SettingKeys.EXIT_DENY_TITLE,
              entity,
              lastLocation));
          trySendSubtitle((Audience) entity, SpongeUtil.valueFor(SettingKeys.EXIT_DENY_SUBTITLE,
              entity,
              lastLocation));
        }
        return;
      }

      // Cannot enter if we are walking into a host and ENTRY is disallowed at the destination
      if (!lastHosts.isEmpty()
          && !SpongeUtil.valueFor(SettingKeys.ENTRY, entity, lastLocation).contains(movementType)) {
        cancelMovement(event, movementType, firstWorld, entity, SettingKeys.ENTRY);
        if (entity instanceof Audience) {
          trySendMessage((Audience) entity, SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_MESSAGE,
              entity,
              lastLocation));
          trySendTitle((Audience) entity, SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_TITLE,
              entity,
              lastLocation));
          trySendSubtitle((Audience) entity, SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_SUBTITLE,
              entity,
              lastLocation));
        }
        return;
      }

      // We are not cancelling, so let's deal with greetings and farewells
      if (entity instanceof Audience) {
        final Audience audience = (Audience) entity;
        for (Host host : lastHosts) {
          host.getValue(SettingKeys.GREETING).ifPresent(stringUnary ->
              trySendMessage(audience, stringUnary.get()));
          host.getValue(SettingKeys.GREETING_TITLE).ifPresent(stringUnary ->
              trySendTitle(audience, stringUnary.get()));
          host.getValue(SettingKeys.GREETING_SUBTITLE).ifPresent(stringUnary ->
              trySendSubtitle(audience, stringUnary.get()));
        }
        for (Host host : firstHosts) {
          host.getValue(SettingKeys.FAREWELL).ifPresent(stringUnary ->
              trySendMessage(audience, stringUnary.get()));
          host.getValue(SettingKeys.FAREWELL_TITLE).ifPresent(stringUnary ->
              trySendTitle(audience, stringUnary.get()));
          host.getValue(SettingKeys.FAREWELL_SUBTITLE).ifPresent(stringUnary ->
              trySendSubtitle(audience, stringUnary.get()));
        }
      }
    }
  }

  private void teleportEntityTo(Entity entity, ServerLocation location) {
    Sponge.server().scheduler().submit(Task.builder()
        .execute(() -> entity.setLocation(location))
        .plugin(SpongeNope.instance().pluginContainer())
        .delay(Ticks.of(1))
        .build());
  }

  private void trySendMessage(Audience audience, String message) {
    if (!message.isEmpty()) {
      audience.sendMessage(Component.text(message));
    }
  }

  private void trySendTitle(Audience audience, String message) {
    if (!message.isEmpty()) {
      audience.sendTitlePart(TitlePart.TITLE, Component.text(message));
    }
  }

  private void trySendSubtitle(Audience audience, String message) {
    if (!message.isEmpty()) {
      audience.sendTitlePart(TitlePart.SUBTITLE, Component.text(message));
    }
  }

  private void cancelMovement(MoveEntityEvent event,
                              Movement movementType,
                              ServerWorld returnWorld,
                              Entity entity,
                              SettingKey<? extends AltSet<Movement>, ?, ?> settingKey) {
    event.setCancelled(true);
    event.setDestinationPosition(event.originalPosition());
    entity.remove(Keys.VEHICLE);
    entity.remove(Keys.PASSENGERS);
    if (movementType.teleportation()) {
      teleportEntityTo(entity, ServerLocation.of(returnWorld,
          event.originalPosition().x(),
          event.originalPosition().y(),
          event.originalPosition().z()));
    }
    SettingEventContext<AltSet<Movement>, MoveEntityEvent> context =
        new SettingEventContextImpl<>(event, settingKey);
    context.report(SettingEventReport.restricted().build());
  }
}
