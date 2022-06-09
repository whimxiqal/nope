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

package me.pietelite.nope.sponge.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.TraverseHostsEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.entity.MoveEntityEvent;

/**
 * Implementation of the {@link TraverseHostsEvent}.
 */
public class TraverseHostsEventImpl implements TraverseHostsEvent {

  private final MoveEntityEvent baseEvent;
  private final Collection<String> entering;
  private final Collection<String> exiting;
  private final Collection<Component> messages;
  private final Cause cause;
  private final Title title;
  private boolean cancelled = false;

  public TraverseHostsEventImpl(MoveEntityEvent baseEvent, Collection<String> entering,
                                Collection<String> exiting, Title title, Collection<Component> messages) {
    this.baseEvent = baseEvent;
    this.entering = Collections.unmodifiableCollection(entering);
    this.exiting = Collections.unmodifiableCollection(exiting);
    this.messages = Collections.unmodifiableCollection(messages);
    this.cause = Cause.builder().from(baseEvent.cause())
        .append(SpongeNope.instance().pluginContainer())
        .build();
    this.title = title;
  }

  @Override
  public MoveEntityEvent baseEvent() {
    return this.baseEvent;
  }

  @Override
  public Collection<String> entering() {
    return entering;
  }

  @Override
  public Collection<String> exiting() {
    return exiting;
  }

  @Override
  public Optional<Title> title() {
    return Optional.ofNullable(title);
  }

  @Override
  public Collection<Component> messages() {
    return messages;
  }

  @Override
  public Cause cause() {
    return this.cause;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

}
