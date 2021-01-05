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

package com.minecraftonline.nope.permission;

/**
 * Enumeration of all {@link Permission}s.
 */
public final class Permissions {

  private Permissions() {
  }


  // Command permissions
  public static final Permission COMMAND_ROOT = Permission.of("nope.command");

  public static final Permission RELOAD = Permission.of("nope.command.reload");

  public static final Permission REGION = Permission.of("nope.command.region");

  public static final Permission CREATE_REGION = Permission.of("nope.command.region.create");

  public static final Permission EDIT_REGION = Permission.of("nope.command.region.edit");

  public static final Permission LIST_REGIONS = Permission.of("nope.command.region.list");

  public static final Permission INFO_REGION = Permission.of("nope.command.region.info");

  public static final Permission DELETE_REGION = Permission.of("nope.command.region.delete");

}
