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

package me.pietelite.nope.sponge.storage.configurate;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.HostSystem;
import me.pietelite.nope.common.host.Universe;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.storage.DataHandler;
import me.pietelite.nope.common.storage.DomainDataHandler;
import me.pietelite.nope.common.storage.UniverseDataHandler;
import me.pietelite.nope.common.storage.ZoneDataHandler;
import org.spongepowered.api.Sponge;

/**
 * A {@link DataHandler} for managing the plugin with Configurate
 * (org.spongepowered.configurate.).
 */
@AllArgsConstructor
public abstract class ConfigurateDataHandler implements DataHandler {

  private final UniverseConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final ZoneConfigurateDataHandler zoneDataHandler;

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
