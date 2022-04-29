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
import me.pietelite.nope.common.api.NopeServiceProvider;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.HostSystem;
import me.pietelite.nope.common.host.Global;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.storage.DataHandler;
import me.pietelite.nope.common.storage.DomainDataHandler;
import me.pietelite.nope.common.storage.ProfileDataHandler;
import me.pietelite.nope.common.storage.UniverseDataHandler;
import me.pietelite.nope.common.storage.SceneDataHandler;
import org.spongepowered.api.Sponge;

/**
 * A {@link DataHandler} for managing the plugin with Configurate
 * (org.spongepowered.configurate.).
 */
public abstract class ConfigurateDataHandler implements DataHandler {

  private final ProfileConfigurateDataHandler profileConfigurateDataHandler;
  private final GlobalConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final SceneConfigurateDataHandler sceneConfigurateDataHandler;

  protected ConfigurateDataHandler(ProfileConfigurateDataHandler profileConfigurateDataHandler,
                                   GlobalConfigurateDataHandler universeDataHandler,
                                   DomainConfigurateDataHandler domainDataHandler,
                                   SceneConfigurateDataHandler sceneConfigurateDataHandler) {
    this.profileConfigurateDataHandler = profileConfigurateDataHandler;
    this.universeDataHandler = universeDataHandler;
    this.domainDataHandler = domainDataHandler;
    this.sceneConfigurateDataHandler = sceneConfigurateDataHandler;
  }

  @Override
  public ProfileDataHandler profiles() {
    return profileConfigurateDataHandler;
  }

  @Override
  public UniverseDataHandler universe() {
    return universeDataHandler;
  }

  @Override
  public DomainDataHandler domains() {
    return domainDataHandler;
  }

  @Override
  public SceneDataHandler scenes() {
    return sceneConfigurateDataHandler;
  }

  @Override
  public HostSystem loadSystem() {
    HostSystem hostSystem = new HostSystem();
    profileConfigurateDataHandler.load().forEach(profile -> hostSystem.profiles().put(profile.name(), profile));
    hostSystem.global(universeDataHandler.load());
    List<Domain> domains = Sponge.server()
        .worldManager()
        .worlds()
        .stream()
        .map(world -> new Domain("_" + world.key()
            .formatted()
            .replace(":", "_"),
            NopeServiceProvider.service().evaluator().unarySettingGlobal(SettingKeys.CACHE_SIZE.name(), Integer.class)))
        .collect(Collectors.toList());
    domains.forEach(domain -> {
      domainDataHandler.load(domain);
      hostSystem.domains().put(domain.name(), domain);
    });
    hostSystem.addAllScenes(sceneConfigurateDataHandler.load());
    return hostSystem;
  }

}
