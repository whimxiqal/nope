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

package me.pietelite.nope.common.gui.volume;

import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.struct.Direction;

/**
 * A thread-save interactive {@link Volume} that allows a user to expand and contract it
 * in any way, so it may be reshaped in real-time before it is used in a
 * {@link me.pietelite.nope.common.host.Scene}.
 *
 * @param <V> the volume type
 */
public abstract class InteractiveVolume<V extends Volume> {

  protected final InteractiveVolumeInfo info;
  protected final int minimumDimension;
  private final Scene scene;
  private V volume;
  private int consecutiveCount = 0;
  private int lastDelta;
  private long lastRequestTime;
  private Direction lastDirection;

  public InteractiveVolume(Scene scene, V volume, InteractiveVolumeInfo info, int minimumDimension) {
    this.scene = scene;
    this.volume = volume;
    this.info = info;
    this.minimumDimension = minimumDimension;
  }

  private int actionDelta(Direction direction) {
    long currentTime = System.currentTimeMillis();
    if (direction != lastDirection || currentTime - lastRequestTime > info.consecutiveTimeout()) {
      // Different direction or took too long
      consecutiveCount = 0;
      lastDelta = info.baseDelta();
    } else if (consecutiveCount < info.consecutiveCutoff()) {
      ++consecutiveCount;
      lastDelta = info.baseDelta();
    } else {
      ++consecutiveCount;
      lastDelta = Math.min(lastDelta * info.consecutiveDeltaMultiplier(), info.maxDelta());
    }
    lastRequestTime = currentTime;
    lastDirection = direction;
    return lastDelta;
  }

  /**
   * Expand the volume in the given direction, applying a multiplier appropriately
   * if this is called quickly enough.
   *
   * @param direction the direction
   */
  public final void expand(Direction direction) {
    expand(direction, actionDelta(direction));
  }

  protected abstract void expand(Direction direction, float count);

  /**
   * Contract the volume in the given direction, applying a multiplier appropriately
   * if this is called quickly enough.
   *
   * @param direction the direction
   */
  public final void contract(Direction direction) {
    contract(direction, actionDelta(direction));
  }

  protected abstract void contract(Direction direction, float count);

  protected final void update(V volume) {
    this.volume.expire();
    this.volume.copyUuidTo(volume);
    this.volume = volume;
  }

  public Scene scene() {
    return scene;
  }

  public V volume() {
    return volume;
  }

}
