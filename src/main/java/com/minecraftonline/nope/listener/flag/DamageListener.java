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
