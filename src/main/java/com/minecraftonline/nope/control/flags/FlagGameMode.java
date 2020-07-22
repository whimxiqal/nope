package com.minecraftonline.nope.control.flags;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;

public class FlagGameMode extends Flag<GameMode> {
  public FlagGameMode(GameMode value) {
    super(value, GameMode.class);
  }

  public FlagGameMode(GameMode value, TargetGroup group) {
    super(value, GameMode.class, group);
  }

  @Override
  public String serialize(Flag<GameMode> flag) {
    return flag.getValue().getName();
  }
}
