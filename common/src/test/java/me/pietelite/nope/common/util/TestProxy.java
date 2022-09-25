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
 *
 */

package me.pietelite.nope.common.util;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.pietelite.nope.common.MockNope;

public class TestProxy implements Proxy {

  private final MockNope mockNope;

  public TestProxy(MockNope mockNope) {
    this.mockNope = mockNope;
  }

  @Override
  public boolean hasPermission(UUID playerUuid, String permission) {
    return mockNope.permissions.getOrDefault(playerUuid, Collections.emptySet()).contains(permission);
  }

  @Override
  public void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit) {
    // ignore
  }

  @Override
  public String version() {
    return null;
  }

  @Override
  public Optional<String> uuidToPlayer(UUID uuid) {
    return Optional.empty();
  }

  @Override
  public Optional<String> uuidToOnlinePlayer(UUID uuid) {
    return Optional.empty();
  }
}
