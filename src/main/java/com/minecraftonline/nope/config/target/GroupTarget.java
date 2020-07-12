package com.minecraftonline.nope.config.target;

import org.spongepowered.api.entity.living.player.Player;

public class GroupTarget implements Target {
  private String groupName;

  public GroupTarget(String group) {
    this.groupName = group;
  }

  @Override
  public boolean isTargeted(Player player) {
    return player.hasPermission("group." + groupName);
  }

  @Override
  public String serialize() {
    return "g:" + groupName;
  }
}
