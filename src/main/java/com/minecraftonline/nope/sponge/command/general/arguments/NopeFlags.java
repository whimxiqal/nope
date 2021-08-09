package com.minecraftonline.nope.sponge.command.general.arguments;

import org.spongepowered.api.command.parameter.managed.Flag;

public class NopeFlags {
  public static final Flag HOST_INFER_FLAG = Flag.builder()
      .aliases("z", "zone")
      .setParameter(NopeParameters.HOST_INFER)
      .build();

  public static final Flag PRIORITY_FLAG = Flag.builder()
      .aliases("p", "priority")
      .setParameter(NopeParameters.PRIORITY)
      .build();
}
