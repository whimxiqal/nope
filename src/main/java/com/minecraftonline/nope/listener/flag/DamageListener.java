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

package com.minecraftonline.nope.listener.flag;

import com.minecraftonline.nope.control.Settings;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class DamageListener extends FlagListener {

  @Listener
  public void onDamage(DamageEntityEvent e) {
    Entity targetEntity = e.getTargetEntity();
    Location<World> targetLoc = targetEntity.getLocation();
    Object root = e.getCause().root();
    if (targetEntity instanceof Player) {
      if (root instanceof Player) {
        // If one of them is in but not the other, still don't allow it.
        e.setCancelled(shouldCancel(Settings.FLAG_PVP, targetLoc, root)
            || shouldCancel(Settings.FLAG_PVP, ((Player)root).getLocation(), root));
      }
      else if (root instanceof Living) {
        e.setCancelled(shouldCancel(Settings.FLAG_MOB_DAMAGE, targetLoc, root));
      }
      else {
        // TODO: Enviroment damage against players
      }
    }
    else if (targetEntity instanceof Monster) {
      // TODO: stop players hitting mobs
    }
    else if (targetEntity instanceof Animal) {
      e.setCancelled(shouldCancel(Settings.FLAG_DAMAGE_ANIMALS, targetLoc, root));
    }
    else if (targetEntity instanceof ItemFrame) {
      e.setCancelled(shouldCancel(Settings.FLAG_ENTITY_ITEM_FRAME_DESTROY, targetLoc, root));
    }
    else if (targetEntity instanceof Painting) {
      e.setCancelled(shouldCancel(Settings.FLAG_ENTITY_PAINTING_DESTROY, targetLoc, root));
    }
  }
}
