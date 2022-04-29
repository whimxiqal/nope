package me.pietelite.nope.common.setting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Targetable {

  @Nullable
  Target target();

  void target(@Nullable Target target);

}
