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

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.HostSystem;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.storage.DataHandler;
import me.pietelite.nope.common.storage.DomainDataHandler;
import me.pietelite.nope.common.storage.Expirable;
import me.pietelite.nope.common.storage.ProfileDataHandler;
import me.pietelite.nope.common.storage.SceneDataHandler;
import me.pietelite.nope.common.storage.UniverseDataHandler;
import org.spongepowered.api.Sponge;

/**
 * A {@link DataHandler} for managing the plugin with Configurate
 * (org.spongepowered.configurate.).
 */
public abstract class ConfigurateDataHandler implements DataHandler {

  private final GlobalConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final SceneConfigurateDataHandler sceneConfigurateDataHandler;
  private final ProfileConfigurateDataHandler profileConfigurateDataHandler;

  protected ConfigurateDataHandler(GlobalConfigurateDataHandler universeDataHandler,
                                   DomainConfigurateDataHandler domainDataHandler,
                                   SceneConfigurateDataHandler sceneConfigurateDataHandler,
                                   ProfileConfigurateDataHandler profileConfigurateDataHandler) {
    this.profileConfigurateDataHandler = profileConfigurateDataHandler;
    this.universeDataHandler = universeDataHandler;
    this.domainDataHandler = domainDataHandler;
    this.sceneConfigurateDataHandler = sceneConfigurateDataHandler;
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
  public SceneDataHandler scenes(String scope) {
    return sceneConfigurateDataHandler;
  }

  @Override
  public ProfileDataHandler profiles(String scope) {
    return profileConfigurateDataHandler;
  }

  @Override
  public void loadSystem(HostSystem system) {
    // Expire profiles and hosts, so they can't be used by other places holding references
    if (system.global() != null) {
      system.global().expire();
    }
    system.domains().values().forEach(Expirable::expire);
    system.scopes().values().forEach(scope -> {
      scope.scenes().values().forEach(Expirable::expire);
      scope.profiles().values().forEach(Expirable::expire);
    });

    // Clear them from the system
    system.domains().clear();
    system.scopes().values().forEach(scope -> {
      scope.scenes().clear();
      scope.profiles().clear();
    });

    profileConfigurateDataHandler.load().forEach(profile ->
        system.getOrCreateScope(profile.scope()).profiles().put(profile.name(), profile));
    system.global(universeDataHandler.load());
    system.global().globalProfile(Objects.requireNonNull(
        system.getOrCreateScope(Nope.NOPE_SCOPE).profiles().get(Nope.GLOBAL_ID),
        "Global profile couldn't be found!"));
    List<Domain> domains = Sponge.server()
        .worldManager()
        .worlds()
        .stream()
        .map(world -> new Domain("_" + world.key()
            .formatted()
            .replace(":", "_"),
            system.global().globalProfile().getValue(SettingKeys.CACHE_SIZE)
                .orElse(SettingKeys.CACHE_SIZE.defaultValue()).get()))
        .collect(Collectors.toList());
    domains.forEach(domain -> {
      domainDataHandler.load(domain);
      system.domains().put(domain.name(), domain);
    });
    system.loadScenes(sceneConfigurateDataHandler.load());
  }

  protected static Collection<Path> persistentComponentPaths(Path rootPath, String componentsName,
                                                      String fileSuffix) {
    File[] scopeFolders = rootPath.toFile().listFiles();
    if (scopeFolders == null) {
      return Collections.emptyList();
    }
    List<Path> paths = new LinkedList<>();
    for (File scopeFolder : scopeFolders) {
      if (!scopeFolder.isDirectory()) {
        continue;
      }
      String scope = scopeFolder.getName();
      File[] sceneFiles = scopeFolder.toPath().resolve(componentsName).toFile().listFiles();
      if (sceneFiles == null) {
        continue;
      }
      for (File sceneFile : sceneFiles) {
        String[] tokens = sceneFile.getName().split("\\.");
        String type = tokens[tokens.length - 1].toLowerCase();
        if (!type.equals(fileSuffix)) {
          Nope.instance().logger().error("File " + sceneFile.getName() + " is unknown");
        }
        paths.add(sceneFile.toPath());
      }
    }
    return paths;
  }

}
