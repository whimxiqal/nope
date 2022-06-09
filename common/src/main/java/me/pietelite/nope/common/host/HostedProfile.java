/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.host;

import java.util.Objects;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Profile} intended to be included on a {@link Host}.
 * This is a {@link Targetable} because you can add a {@link Target}
 * to override all {@link Target}s that appears on {@link me.pietelite.nope.common.setting.Setting}s
 * in the {@link Profile}, but only for the {@link Host}.
 */
public class HostedProfile implements Targetable {

  private final Profile profile;
  private Target target;

  public HostedProfile(@NotNull Profile profile) {
    this(profile, null);
  }

  public HostedProfile(@NotNull Profile profile, Target target) {
    this.profile = Objects.requireNonNull(profile);
    this.target = target;
  }

  public Profile profile() {
    return profile;
  }

  public Target target() {
    return target;
  }

  @Override
  public void target(@Nullable Target target) {
    this.target = target;
  }

  /**
   * Get the {@link Target} that applies for the given key.
   *
   * @param key the setting key
   * @return the target
   */
  public Target activeTargetFor(SettingKey<?, ?, ?> key) {
    if (profile.getTarget(key).isPresent()) {
      return profile.getTarget(key).get();
    }
    if (target != null) {
      return target;
    }
    return profile.target();
  }

}
