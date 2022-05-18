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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import me.pietelite.nope.common.api.setting.Movement;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.util.TreeUtil;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.TraverseHostsEvent;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventReport;
import me.pietelite.nope.sponge.event.TraverseHostsEventImpl;
import me.pietelite.nope.sponge.listener.SettingEventContextImpl;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
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
        return;
      }

      // Check for EXIT and ENTRY
      Set<Host> firstHosts = SpongeNope.instance().system().containingHosts(firstLocation);
      Set<Host> lastHosts = SpongeNope.instance().system().containingHosts(lastLocation);

      // Remove shared hosts
      Set<Host> shared = new HashSet<>(firstHosts);
      shared.retainAll(lastHosts);
      firstHosts.removeAll(shared);
      lastHosts.removeAll(shared);

      boolean traversingHosts = false; // whether we are exiting or entering any host
      boolean movementCancelled = false;
      SettingKey<? extends AltSet<Movement>, ?, ?> movementCancelCause = null;

      AtomicReference<Component> title = new AtomicReference<>(Component.empty());
      AtomicReference<Component> subTitle = new AtomicReference<>(Component.empty());
      List<Component> messages = new LinkedList<>();

      final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

      // Cannot exit if we are walking out of a host and EXIT is disallowed at the origin
      if (!firstHosts.isEmpty()) {
        traversingHosts = true;
        // We only want to evaluate EXIT if EXIT is set on a host that we are leaving.
        // Otherwise, the EXIT setting doesn't matter for this movement
        if (firstHosts.stream().anyMatch(host -> host.isSet(SettingKeys.EXIT))) {
          if (!SpongeUtil.valueFor(SettingKeys.EXIT, entity, firstLocation).contains(movementType)) {
            movementCancelled = true;
            movementCancelCause = SettingKeys.EXIT;
            SpongeUtil.valueFor(SettingKeys.EXIT_DENY_MESSAGE, entity, firstLocation)
                .ifPresent(message -> messages.add(serializer.deserialize(message)));
            SpongeUtil.valueFor(SettingKeys.EXIT_DENY_TITLE, entity, firstLocation)
                .ifPresent(message -> title.set(serializer.deserialize(message)));
            SpongeUtil.valueFor(SettingKeys.EXIT_DENY_SUBTITLE, entity, firstLocation)
                .ifPresent(message -> subTitle.set(serializer.deserialize(message)));
          }
        }
      }

      // Cannot enter if we are walking into a host and ENTRY is disallowed at the destination
      if (!lastHosts.isEmpty() && !movementCancelled) {
        traversingHosts = true;
        // We only want to evaluate EXIT if EXIT is set on a host that we are leaving.
        // Otherwise, the EXIT setting doesn't matter for this movement
        if (lastHosts.stream().anyMatch(host -> host.isSet(SettingKeys.ENTRY))) {
          if (!SpongeUtil.valueFor(SettingKeys.ENTRY, entity, lastLocation).contains(movementType)) {
            movementCancelled = true;
            movementCancelCause = SettingKeys.ENTRY;
            SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_MESSAGE, entity, lastLocation)
                .ifPresent(message -> messages.add(serializer.deserialize(message)));
            SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_TITLE, entity, lastLocation)
                .ifPresent(message -> title.set(serializer.deserialize(message)));
            SpongeUtil.valueFor(SettingKeys.ENTRY_DENY_SUBTITLE, entity, lastLocation)
                .ifPresent(message -> subTitle.set(serializer.deserialize(message)));
          }
        }
      }

      if (!movementCancelled) {
        lastHosts.forEach(host -> SettingKeys.GREETING.extractValue(Collections.singletonList(host),
                entity.uniqueId()).result()
            .ifPresent(message -> messages.add(serializer.deserialize(message))));
        firstHosts.forEach(host -> SettingKeys.FAREWELL.extractValue(Collections.singletonList(host),
                entity.uniqueId()).result()
            .ifPresent(message -> messages.add(serializer.deserialize(message))));
        Optional<Host> maxPriorityEntering = lastHosts.stream().max(Comparator.comparing(Host::priority));
        Optional<Host> maxPriorityExiting = firstHosts.stream().max(Comparator.comparing(Host::priority));
        if (maxPriorityEntering.isPresent() || maxPriorityExiting.isPresent()) {
          Optional<Component> maxPriorityEnteringTitle = maxPriorityEntering.flatMap(host ->
                  SettingKeys.GREETING_TITLE.extractValue(Collections.singletonList(host),
                      entity.uniqueId()).result())
              .map(serializer::deserialize);
          Optional<Component> maxPriorityEnteringSubtitle = maxPriorityEntering.flatMap(host ->
                  SettingKeys.GREETING_SUBTITLE.extractValue(Collections.singletonList(host),
                      entity.uniqueId()).result())
              .map(serializer::deserialize);
          Optional<Component> maxPriorityExitingTitle = maxPriorityExiting.flatMap(host ->
                  SettingKeys.FAREWELL_TITLE.extractValue(Collections.singletonList(host),
                      entity.uniqueId()).result())
              .map(serializer::deserialize);
          Optional<Component> maxPriorityExitingSubtitle = maxPriorityExiting.flatMap(host ->
                  SettingKeys.FAREWELL_SUBTITLE.extractValue(Collections.singletonList(host),
                      entity.uniqueId()).result())
              .map(serializer::deserialize);
          boolean enteringPrioritized = !maxPriorityExiting.isPresent()
              || (maxPriorityEntering.isPresent()
              && maxPriorityEntering.get().priority() > maxPriorityExiting.get().priority());
          if (enteringPrioritized) {
            // set exiting first so that entering can override it
            maxPriorityExitingTitle.ifPresent(title::set);
            maxPriorityExitingSubtitle.ifPresent(subTitle::set);
            maxPriorityEnteringTitle.ifPresent(title::set);
            maxPriorityEnteringSubtitle.ifPresent(subTitle::set);
          } else {
            // set entering first so that exiting can override it
            maxPriorityEnteringTitle.ifPresent(title::set);
            maxPriorityEnteringSubtitle.ifPresent(subTitle::set);
            maxPriorityExitingTitle.ifPresent(title::set);
            maxPriorityExitingSubtitle.ifPresent(subTitle::set);
          }
        }
      }

      if (traversingHosts) {
        TraverseHostsEvent traverseEvent = new TraverseHostsEventImpl(event,
            lastHosts.stream().map(Host::name).collect(Collectors.toList()),
            firstHosts.stream().map(Host::name).collect(Collectors.toList()),
            Title.title(title.get(), subTitle.get()),
            messages);
        traverseEvent.setCancelled(movementCancelled);

        Sponge.eventManager().post(traverseEvent);

        if (entity instanceof Audience) {
          final Audience audience = (Audience) entity;
          traverseEvent.messages().forEach(audience::sendMessage);
          traverseEvent.title().ifPresent(audience::showTitle);
        }

        if (traverseEvent.isCancelled()) {
          cancelMovement(event, movementType, firstWorld, entity, movementCancelCause);
        }
      }
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
    if (settingKey != null) {
      SettingEventContext<AltSet<Movement>, MoveEntityEvent> context =
          new SettingEventContextImpl<>(event, settingKey);
      context.report(SettingEventReport.restricted().build());
    }
  }

  private void teleportEntityTo(Entity entity, ServerLocation location) {
    Sponge.server().scheduler().submit(Task.builder()
        .execute(() -> entity.setLocation(location))
        .plugin(SpongeNope.instance().pluginContainer())
        .delay(Ticks.of(1))
        .build());
  }
}
