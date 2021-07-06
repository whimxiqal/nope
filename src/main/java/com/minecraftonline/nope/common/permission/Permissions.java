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

package com.minecraftonline.nope.common.permission;

/**
 * Enumeration of all {@link Permission}s.
 */
public final class Permissions {

  public static final Permission COMMAND_CREATE = Permission.of("nope.command.create");
  public static final Permission COMMAND_DESTROY = Permission.of("nope.command.destroy");
  public static final Permission COMMAND_EDIT = Permission.of("nope.command.edit");
  public static final Permission COMMAND_INFO = Permission.of("nope.command.info");
  public static final Permission COMMAND_LIST = Permission.of("nope.command.list");
  public static final Permission COMMAND_SHOW = Permission.of("nope.command.show");
  public static final Permission COMMAND_TELEPORT = Permission.of("nope.command.teleport");
  public static final Permission COMMAND_RELOAD = Permission.of("nope.command.reload");
  public static final Permission COMMAND_SETTING = Permission.of("nope.command.setting");
  public static final Permission UNRESTRICTED = Permission.of("nope.unrestricted");

  private Permissions() {
  }

}
