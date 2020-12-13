/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.control.target;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.api.entity.living.player.Player;

import java.io.Serializable;

public class TargetSet implements Serializable, Cloneable {
  private final Multimap<Target.TargetType, Target> targets = HashMultimap.create();

  public TargetSet() {}

  public boolean isPlayerTargeted(Player player) {
    return targets.values().stream()
        .anyMatch(target -> target.isTargeted(player));
  }

  public void add(Target target) {
    targets.put(target.getTargetType(), target);
  }

  /**
   * Removes a target that matches the given target,
   * which is determined by the equals and hashcode
   * implementation.
   *
   * @param target Target to remove
   * @return Whether a target was removed
   */
  public boolean remove(Target target) {
    return targets.get(target.getTargetType()).remove(target);
  }

  public Multimap<Target.TargetType, Target> getTargets() {
    return targets;
  }
}
