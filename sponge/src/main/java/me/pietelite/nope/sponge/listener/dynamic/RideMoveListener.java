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

package me.pietelite.nope.sponge.listener.dynamic;

import java.util.LinkedList;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.MoveEntityEvent;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#RIDE}, specifically for stopping the ride
 * during motion into a region that does not allow it.
 */
public class RideMoveListener implements SettingEventListener<Boolean, MoveEntityEvent> {
  @Override
  public void handle(SettingEventContext<Boolean, MoveEntityEvent> context) {
    LinkedList<Entity> entityQueue = new LinkedList<>();
    entityQueue.add(context.event().entity().baseVehicle().map(Value::get).orElse(context.event().entity()));
    Entity current;
    Entity vehicle;
    while (!entityQueue.isEmpty()) {
      current = entityQueue.pop();
      if (current.vehicle().isPresent()) {
        vehicle = current.vehicle().get().get();
        if (!context.lookup(current, vehicle.serverLocation())) {
          current.remove(Keys.VEHICLE);
        }
      }
      entityQueue.addAll(current.passengers().get());
    }
  }
}
