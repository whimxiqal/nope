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

package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.HostSystem;
import com.minecraftonline.nope.common.host.Universe;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.storage.DataHandler;
import com.minecraftonline.nope.common.storage.DomainDataHandler;
import com.minecraftonline.nope.common.storage.TemplateDataHandler;
import com.minecraftonline.nope.common.storage.UniverseDataHandler;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.spongepowered.api.Sponge;

@AllArgsConstructor
public abstract class ConfigurateDataHandler implements DataHandler {

  private final UniverseConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final ZoneConfigurateDataHandler zoneDataHandler;
  private final TemplateConfigurateDataHandler templateConfigurateDataHandler;

  @Override
  public UniverseDataHandler universe() {
    return universeDataHandler;
  }

  @Override
  public DomainDataHandler domains() {
    return domainDataHandler;
  }

  @Override
  public ZoneDataHandler zones() {
    return zoneDataHandler;
  }

  @Override
  public TemplateDataHandler templates() {
    return templateConfigurateDataHandler;
  }

  @Override
  public HostSystem loadSystem() {
    Universe universe = universeDataHandler.load();
    List<Domain> domains = Sponge.server()
        .worldManager()
        .worlds()
        .stream()
        .map(world -> new Domain("_" + world.key()
            .formatted()
            .replace(":", "_"),
            SettingKeys.CACHE_SIZE.getDataOrDefault(universe)))
        .collect(Collectors.toList());
    domains.forEach(domainDataHandler::load);
    return new HostSystem(universe, domains);
  }

}
