package com.minecraftonline.nope.sponge.command.parameters;

import org.spongepowered.api.command.parameter.managed.Flag;

public class Flags {
  public static final Flag HOST_INFER_FLAG = Flag.builder()
      .aliases("z", "zone")
      .setParameter(Parameters.HOST_INFER)
      .build();

  public static final Flag PRIORITY = Flag.builder()
      .aliases("p", "priority")
      .setParameter(Parameters.PRIORITY)
      .build();

  public static final Flag PARENT = Flag.builder()
      .aliases("parent", "t")
      .setParameter(Parameters.PARENT)
      .build();

  public static final Flag ADDITIVE_VALUE_FLAG = Flag.of("additive");

  public static final Flag SUBTRACTIVE_VALUE_FLAG = Flag.of("subtractive");

}
