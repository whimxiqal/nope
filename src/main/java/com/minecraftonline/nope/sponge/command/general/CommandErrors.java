package com.minecraftonline.nope.sponge.command.general;

import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;

public final class CommandErrors {

  public static final Supplier<Component> ONLY_PLAYERS = () -> SpongeNope.instance()
      .formatter()
      .error("Only players may execute this command");

  private CommandErrors() {
  }
}
