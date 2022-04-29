package me.pietelite.nope.common.api.edit;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface Alteration {

  enum Result {
    SUCCESS,
    WARNING,
    FAILURE;
  }

  Result result();

  Optional<String> message();

}
