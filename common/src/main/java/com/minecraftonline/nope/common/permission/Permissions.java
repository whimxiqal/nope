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

package com.minecraftonline.nope.common.permission;

import com.minecraftonline.nope.common.permission.Permission;

/**
 * Enumeration of all {@link Permission}s.
 */
public final class Permissions {

  public static final Permission NOPE = Permission.of("nope");
  public static final Permission CREATE = Permission.of("nope.create");
  public static final Permission DESTROY = Permission.of("nope.destroy");
  public static final Permission EDIT = Permission.of("nope.edit");
  public static final Permission INFO = Permission.of("nope.info");
  public static final Permission TELEPORT = Permission.of("nope.teleport");
  public static final Permission RELOAD = Permission.of("nope.reload");
  public static final Permission UNRESTRICTED = Permission.of("nope.unrestricted");

  private Permissions() {
  }

}
