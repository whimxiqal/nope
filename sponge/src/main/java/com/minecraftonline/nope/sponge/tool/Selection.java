/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.tool;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.math.Vector3i;
import com.minecraftonline.nope.common.math.Volume;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(fluent = true)
public abstract class Selection<T extends Volume> {

  Domain domain;
  Vector3i position1;
  Vector3i position2;

  public final Component description() {
    if (!validate()) {
      throw new IllegalStateException("This selection is invalid, so props should not be called");
    }
    return propsWhenValid();
  }

  protected abstract Component propsWhenValid();

  protected abstract T construct();

  public final boolean validate(List<String> errors) {
    if (position1 == null) {
      errors.add("You must select position 1");
    } else {
      if (position1.x() > Nope.WORLD_RADIUS
          || position1.x() < -Nope.WORLD_RADIUS
          || position1.z() > Nope.WORLD_RADIUS
          || position1.z() < -Nope.WORLD_RADIUS) {
        errors.add("Your first selected point is out of the world's radius");
      }
      if (position1.y() > Nope.WORLD_DEPTH) {
        errors.add("Your first selected point is too high");
      }
      if (position1.y() < 0) {
        errors.add("Your first selected point is too low");
      }
    }

    if (position2 == null) {
      errors.add("You must select position 2");
    } else {
      if (position2.x() > Nope.WORLD_RADIUS
          || position2.x() < -Nope.WORLD_RADIUS
          || position2.z() > Nope.WORLD_RADIUS
          || position2.z() < -Nope.WORLD_RADIUS) {
        errors.add("Your second selected point is out of the world's radius");
      }

      if (position2.y() > Nope.WORLD_DEPTH) {
        errors.add("Your second selected point is too high");
      }

      if (position2.y() < 0) {
        errors.add("Your second selected point is too low");
      }
    }
    return errors.isEmpty();
  }

  public final T build() {
    if (validate()) {
      T volume = construct();
      if (volume.valid()) {
        return volume;
      }
    }
    return null;
  }
  public final boolean validate() {
    return validate(new LinkedList<>());
  }

  public final List<String> errors() {
    List<String> errors = new LinkedList<>();
    validate(errors);
    return errors;
  }

  public final boolean isComplete() {
    return domain != null && position1 != null && position2 != null;
  }
}
