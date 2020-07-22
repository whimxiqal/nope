package com.minecraftonline.nope.arguments;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArgsUtil {
  /**
   * Filters possibilities by beginning match
   * @param typed String that was typed
   * @param choices possible completions
   * @return Possible completions, or null if its already a match
   */
  @Nullable
  public static List<String> filterPossibilities(String typed, Collection<String> choices) {
    List<String> result = new ArrayList<>();
    for (String s : choices) {
      if (typed.equals(s)) {
        return null;
      }
      else if (s.startsWith(typed)) {
        result.add(s);
      }
    }
    return result;
  }
}
