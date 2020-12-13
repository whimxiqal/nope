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

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.RegionSet;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.FlagBoolean;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.Membership;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class DamageListener extends FlagListener {

  @Listener
  public void onDamage(DamageEntityEvent e) {
    Entity targetEntity = e.getTargetEntity();
    Location<World> targetLoc = targetEntity.getLocation();
    Object root = e.getCause().root();
    if (root instanceof Player) {
      if (Nope.getInstance().canOverrideRegion((Subject)root)) {
        return; // Force allow
      }
    }
    if (targetEntity instanceof Player) {
      if (root instanceof Player) {
        Player player = (Player)root;
        // If one of them is in but not the other, still don't allow it.
        Membership membership = Membership.player(player);
        e.setCancelled(shouldCancel(Settings.FLAG_PVP, targetLoc, membership)
            || shouldCancel(Settings.FLAG_PVP, ((Player)root).getLocation(), membership));
        return;
      }
      if (shouldCancel(Settings.FLAG_EVP, targetEntity.getLocation(), Membership.NONE)) {
        e.setCancelled(true);
      }
      else if (root instanceof Living) {
        e.setCancelled(shouldCancel(Settings.FLAG_MOB_DAMAGE, targetLoc, Membership.NONE));
      }
    }
    else if (targetEntity instanceof Creature) {
      RegionSet regionSet = Nope.getInstance().getGlobalHost().getWorld(e.getTargetEntity().getWorld())
          .getRegions(e.getTargetEntity().getLocation().getPosition());
      Membership membership = root instanceof Player ? Membership.player((Player)root) : Membership.NONE;
      if (targetEntity instanceof Animal) {
        e.setCancelled(!regionSet.findFirstFlagSettingOrDefault(Settings.FLAG_DAMAGE_ANIMALS, membership).getValue());
      }
      else {
        e.setCancelled(!regionSet.findFirstFlagSettingOrDefault(Settings.FLAG_PVE, membership).getValue());
      }
    }
    else {
      Membership membership = root instanceof Player ? Membership.player((Player)root) : Membership.NONE;
      Setting<FlagState> subSetting;
      if (targetEntity instanceof ItemFrame) subSetting = Settings.FLAG_ENTITY_ITEM_FRAME_DESTROY;
      else if (targetEntity instanceof Painting) subSetting = Settings.FLAG_ENTITY_PAINTING_DESTROY;
      else if (targetEntity instanceof ArmorStand) subSetting = Settings.FLAG_ENTITY_ARMOR_STAND_DESTROY;
      else if (targetEntity instanceof Boat || targetEntity instanceof Minecart) subSetting = Settings.FLAG_VEHICLE_DESTROY;
      else {
        return;
      }
      RegionSet regionSet = Nope.getInstance().getGlobalHost().getWorld(e.getTargetEntity().getWorld())
          .getRegions(e.getTargetEntity().getLocation().getPosition());
      // We could do something like check if mobs is in the region but i think that is not the point of this.
      // So we just say no one has permission to do these things (except those with override permission)
      e.setCancelled(
          !regionSet.findFirstFlagSettingOrDefault(subSetting, membership).getValue()
      );
    }
  }
}
