package com.minecraftonline.nope.control.target;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.api.entity.living.player.Player;

public class TargetSet {
  private Multimap<Target.TargetType, Target> targets = HashMultimap.create();

  public TargetSet() {}

  public boolean isPlayerTargeted(Player player) {
    return targets.values().stream()
        .anyMatch(target -> target.isTargeted(player));
  }

  public void add(Target target) {
    targets.put(target.getTargetType(), target);
  }

  public Multimap<Target.TargetType, Target> getTargets() {
    return targets;
  }
}
