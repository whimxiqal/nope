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

package me.pietelite.nope.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.pietelite.nope.common.api.NopeServiceConsumer;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.util.MockDataHandler;
import me.pietelite.nope.common.util.TestLogger;
import me.pietelite.nope.common.util.TestProxy;

/**
 * A test plugin class.
 */
public class MockNope extends Nope {

  public static final String DOMAIN_1 = "domain_1";
  public static final String DOMAIN_2 = "domain_2";
  public final Map<UUID, Set<String>> permissions = new HashMap<>();

  public MockNope() {
    super(new TestLogger());
  }

  /**
   * A static factory for this class, given a set of domains.
   *
   * @param domainNames the domains in the test system
   * @return the plugin instance
   */
  public static MockNope init(String... domainNames) {
    MockNope nope = new MockNope();
    Nope.instance(nope);
    nope.proxy(new TestProxy(nope));
    NopeServiceConsumer.consume(new NopeServiceImpl());
    SettingKeys.registerTo(instance().settingKeys());
    nope.data(new MockDataHandler());
    nope.data().loadSystem(nope.system());
    return nope;
  }

  public void registerPermission(UUID playerUuid, String permission) {
    permissions.computeIfAbsent(playerUuid, k -> new HashSet<>()).add(permission);
  }

}
