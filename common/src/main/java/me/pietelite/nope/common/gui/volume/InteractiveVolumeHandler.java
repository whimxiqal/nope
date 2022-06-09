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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.struct.Direction;

/**
 * A state holder for {@link InteractiveVolume}s with which are being interacted
 * by players in-game.
 */
public class InteractiveVolumeHandler {

  private final Map<UUID, InteractiveVolume<?>> interactionMap = new HashMap<>();
  private final InteractiveVolumeInfo info;

  public InteractiveVolumeHandler(InteractiveVolumeInfo info) {
    this.info = info;
  }

  /**
   * Begin a session of interacting with a volume.
   *
   * @param uuid           the uuid of the player
   * @param scene          the scene for which the volume is a part
   * @param startingVolume the volume being edited
   */
  public void beginSession(UUID uuid, Scene scene, Volume startingVolume) {
    switch (startingVolume.zoneType()) {
      case CUBOID:
        interactionMap.put(uuid, new InteractiveCuboid(scene, (Cuboid) startingVolume, info, 1));
        break;
      case CYLINDER:
      case SLAB:
      case SPHERE:
      default:
        throw new UnsupportedOperationException();
    }
  }

  public boolean hasSession(UUID uuid) {
    return interactionMap.containsKey(uuid);
  }

  public void expand(UUID uuid, Direction direction) {
    interactionMap.get(uuid).expand(direction);
  }

  public void contract(UUID uuid, Direction direction) {
    interactionMap.get(uuid).contract(direction);
  }

  public Volume currentVolume(UUID uuid) {
    return interactionMap.get(uuid).volume();
  }

  public InteractiveVolume<?> finishSession(UUID uuid) {
    return interactionMap.remove(uuid);
  }

  /**
   * Get all players who are currently editing volumes and the current state of the volume
   * they are editing.
   *
   * @return the map
   */
  public Map<UUID, Volume> volumes() {
    return interactionMap.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().volume()));
  }

}
