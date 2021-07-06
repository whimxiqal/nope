package com.minecraftonline.nope.sponge.util;

import java.util.Optional;
import org.spongepowered.api.service.context.Context;

public final class Hosts {

  public static String nameToContextKey(String name) {
    return "nope.host." + name;
  }

  /**
   * Statically convert a {@link Context} key into the name of a host.
   *
   * @param key the context key
   * @return the name of the encoded host
   */
  public static Optional<String> contextKeyToName(String key) {
    if (!isContextKey(key)) {
      return Optional.empty();
    }
    return Optional.of(key.substring(10));
  }

  /**
   * Checks whether the given string is a context key encoding a host's name.
   *
   * @param key the string that may be a key
   * @return true if key
   */
  public static boolean isContextKey(String key) {
    if (key == null || key.length() < 11) {
      return false;
    }
    return key.startsWith("nope.host.");
  }

  private Hosts() {
  }
}
