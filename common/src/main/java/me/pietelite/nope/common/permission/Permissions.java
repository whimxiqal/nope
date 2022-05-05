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

package me.pietelite.nope.common.permission;

/**
 * Enumeration of all {@link Permission}s.
 */
public final class Permissions {

  public static final Permission HOST_CREATE = Permission.of("nope.host.create");
  public static final Permission HOST_DESTROY = Permission.of("nope.host.destroy");
  public static final Permission HOST_EDIT = Permission.of("nope.host.edit");
  public static final Permission HOST_INFO = Permission.of("nope.host.info");
  public static final Permission PROFILE_CREATE = Permission.of("nope.profile.create");
  public static final Permission PROFILE_DESTROY = Permission.of("nope.profile.destroy");
  public static final Permission PROFILE_EDIT = Permission.of("nope.profile.create");
  public static final Permission PROFILE_INFO = Permission.of("nope.profile.info");
  public static final Permission DEBUG = Permission.of("nope.debug");
  public static final Permission RELOAD = Permission.of("nope.reload");
  public static final Permission UNRESTRICTED = Permission.of("nope.unrestricted");

  private Permissions() {
  }

}
