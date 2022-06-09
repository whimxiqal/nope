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

package me.pietelite.nope.sponge.api.event;

import java.util.Collection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.MoveEntityEvent;

/**
 * An {@link Event} to represent when a player enters or leaves one or more hosts.
 */
public interface TraverseHostsEvent extends Event, Cancellable {

  /**
   * The move entity event that caused this event.
   *
   * @return the base event
   */
  MoveEntityEvent baseEvent();

  /**
   * Get an immutable collection of the names of the hosts into which the player
   * moved during this event.
   *
   * @return the entered hosts
   */
  Collection<String> entering();

  /**
   * Get an immutable collection of the names of the hosts out of which the player
   * moved during the event.
   *
   * @return the existed hosts
   */
  Collection<String> exiting();

  /**
   * Get the title that will be sent to the player due to this movement.
   *
   * @return the title, or an empty optional if none will be sent
   */
  Optional<Title> title();

  /**
   * Get all the messages that will be sent to the player due to this movement.
   *
   * @return all messages
   */
  Collection<Component> messages();

}
