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

package me.pietelite.nope.common.setting.sets;

import me.pietelite.nope.common.struct.Described;
import me.pietelite.nope.common.struct.HashAltSet;

public class MovementSet extends HashAltSet.FewEnum<MovementSet.Movement> {

  public MovementSet() {
    super(Movement.class);
  }

  /**
   * Enumeration for all movement types considered by Nope.
   */
  public enum Movement implements Described {
    CHORUSFRUIT("Teleportation with chorus fruit", true),
    COMMAND("Movement caused by a command", true),
    ENDGATEWAY("Teleportation through an end gateway", true),
    ENDERPEARL("Teleportation with an ender pearl", true),
    ENTITYTELEPORT("Teleportation of an entity", true),
    NATURAL("Any natural movement", false),
    PLUGIN("Movement caused by a plugin", true),
    PORTAL("Teleportation with a nether portal", true);

    private final String description;
    private final boolean teleportation;

    Movement(String description, boolean teleportation) {
      this.description = description;
      this.teleportation = teleportation;
    }

    @Override
    public String description() {
      return description;
    }

    @Override
    public String toString() {
      return name().toLowerCase();
    }

    public boolean teleportation() {
      return this.teleportation;
    }
  }
}
