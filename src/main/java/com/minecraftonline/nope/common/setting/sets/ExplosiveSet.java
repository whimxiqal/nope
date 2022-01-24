/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.common.setting.sets;

import com.minecraftonline.nope.common.struct.Described;
import com.minecraftonline.nope.common.struct.HashAltSet;

public class ExplosiveSet extends HashAltSet.FewEnum<ExplosiveSet.Explosive> {

  public ExplosiveSet() {
    super(Explosive.class);
  }

  /**
   * Enumeration for all explosive types considered by Nope.
   */
  public enum Explosive implements Described {
    CREEPER("Explosion caused by creeper"),
    ENDERCRYSTAL("Explosion caused by endercrystal"),
    FIREWORK("Explosion caused by firework"),
    LARGEFIREBALL("Explosion caused by large fireball"),
    PRIMEDTNT("Explosion caused by primed TNT"),
    TNTMINECART("Explosion caused by TNT minecart"),
    WITHER("Explosion caused by Wither"),
    WITHERSKULL("Explosion caused by Wither skull");

    private final String description;

    Explosive(String description) {
      this.description = description;
    }

    @Override
    public String description() {
      return description;
    }
  }
}
