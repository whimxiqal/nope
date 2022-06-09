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

package me.pietelite.nope.common.host;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.ScopeEditor;
import me.pietelite.nope.common.api.edit.SystemEditor;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.IgnoreCaseStringHashMap;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.util.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The central controlling object of almost all of Nope's state.
 */
public class HostSystem {

  private final IgnoreCaseStringHashMap<Domain> domains = new IgnoreCaseStringHashMap<>();
  private final IgnoreCaseStringHashMap<Scope> scopes = new IgnoreCaseStringHashMap<>();

  private Global global;

  public HostSystem() {
    scopes.put(Nope.NOPE_SCOPE, new Scope(Nope.NOPE_SCOPE));
  }

  public Global global() {
    return global;
  }

  public void global(Global global) {
    this.global = global;
  }

  public IgnoreCaseStringHashMap<Domain> domains() {
    return domains;
  }

  /**
   * Get the {@link Scope} with the given name or create a new one if one doesn't exist.
   *
   * @param name the name of the scope
   * @return the scope
   */
  public Scope getOrCreateScope(String name) {
    Scope scope = scopes.get(name);
    if (scope == null) {
      scope = new Scope(name);
      scopes.put(name, scope);
    }
    return scope;
  }

  public Scope scope(String name) {
    return scopes.get(name);
  }

  /**
   * Register a new scope with the given name.
   *
   * @param name the name of the scope
   * @throws IllegalArgumentException if a scope already exists with that name
   */
  public void registerScope(String name) throws IllegalArgumentException {
    if (scopes.containsKey(name)) {
      throw new IllegalArgumentException("The name " + name + " is already registered");
    }
    scopes.put(name, new Scope(name));
  }

  public IgnoreCaseStringHashMap<Scope> scopes() {
    return scopes;
  }

  /**
   * Returns a list of every host.
   *
   * @return all hosts
   */
  @NotNull
  public List<Host> hosts() {
    List<Host> hosts = new LinkedList<>();
    hosts.add(this.global);
    hosts.addAll(this.domains.values());
    scopes.values().forEach(scope -> hosts.addAll(scope.scenes().values()));
    return hosts;
  }

  /**
   * Get all hosts under the given scope.
   *
   * @param scopeName the name of the scope
   * @return the map of hosts, keyed by host name
   * @throws IllegalArgumentException if no scope exists with that name
   */
  public Map<String, Host> hosts(String scopeName) throws IllegalArgumentException {
    if (!scopes.containsKey(scopeName)) {
      throw new IllegalArgumentException("There is no scope named " + scopeName);
    }
    Map<String, Host> hosts = new HashMap<>();
    hosts.put(this.global.name, this.global);
    hosts.putAll(this.domains.map());
    hosts.putAll(scope(scopeName).scenes().map());
    return hosts;
  }

  /**
   * Add a volume into a scene and updates the internal structures
   * to be aware of the scene's increased ownership of a domain.
   *
   * @param volume the volume to add on the scene
   * @param scene  the scene
   * @return the index at which the volume was added for the scene
   */
  public int addVolume(Volume volume, Scene scene) {
    LinkedList<Volume> newVolumes = new LinkedList<>();
    for (Volume oldVolume : scene.volumes()) {
      if (oldVolume.uuid().equals(volume.uuid())) {
        assert oldVolume.domain() == volume.domain();
        volume.domain().volumes().remove(oldVolume, false);
      } else {
        newVolumes.add(oldVolume);
      }
    }
    newVolumes.add(volume);
    scene.volumes(newVolumes);
    volume.domain().volumes().put(volume, scene, true);
    scene.save();
    return newVolumes.size() - 1;
  }

  /**
   * Get all the hosts "superior" at that location, which include any hosts
   * that encapsulate that point.
   *
   * @param location the location for which to find superior hosts
   * @return the superior hosts
   */
  @NotNull
  public Set<Host> containingHosts(@NotNull Location location) {
    Set<Host> set = new HashSet<>();
    set.add(global);
    set.add(location.domain());

    // Add all the containing scenes and their parents
    Set<Scene> scenes = location.domain()
        .volumes()
        .containing(location.posX(), location.posY(), location.posZ());
    set.addAll(scenes);
    return set;
  }

  /**
   * Determine if the key is assigned to any host.
   *
   * @param key the key
   * @return true if a host has it assigned anywhere
   */
  public boolean isAssigned(SettingKey<?, ?, ?> key) {
    return hosts().stream().anyMatch(host ->
        host.hostedProfiles().stream().anyMatch(profileItem ->
            profileItem.profile().get(key).isPresent()));
  }

  /**
   * Determine whether a host exists under a given scope.
   *
   * @param scope the scope
   * @param host  the host
   * @return true if the name exists
   */
  public boolean hasName(String scope, String host) {
    if (global.name().equalsIgnoreCase(host)) {
      return true;
    }
    if (domains().containsKey(host)) {
      return true;
    }
    return scope(scope).scenes().containsKey(host);
  }

  /**
   * Load a series of scenes into the system.
   *
   * @param scenes the scenes
   */
  public void loadScenes(Iterable<Scene> scenes) {
    // Put all scenes in the collection of scenes for indexing by their name
    scenes.forEach(scene -> getOrCreateScope(scene.scope()).scenes().put(scene.name().toLowerCase(), scene));

    Set<Domain> domains = new HashSet<>();
    // Add all volumes into volume tree
    scenes.forEach(scene -> scene.volumes().forEach(volume -> {
      volume.domain().volumes().put(volume, scene, false);
      domains.add(volume.domain());
    }));
    // Construct all volume trees that were affected
    domains.forEach(domain -> domain.volumes().construct());
  }


  public <X> Evaluation<X> lookupAnonymous(@NotNull SettingKey<X, ?, ?> key,
                                           @NotNull Location location) {
    return lookup(key, null, location);
  }

  /**
   * Evaluate the result of a setting key for a specific user at a given location.
   * This method is the meat and potatoes of the plugin.
   *
   * @param key      the key
   * @param userUuid the user's uuid
   * @param location the location
   * @param <X>      the type of data to return
   * @return a record of the evaluation process
   */
  public <X> Evaluation<X> lookup(@NotNull final SettingKey<X, ?, ?> key,
                                  @Nullable final UUID userUuid,
                                  @NotNull final Location location) {
    Set<Scene> containingScenes = location.domain()
        .volumes()
        .containing(location.posX(),
            location.posY(),
            location.posZ());

    return lookup(key, userUuid, location.domain(), containingScenes);
  }

  private <X> Evaluation<X> lookup(SettingKey<X, ?, ?> key, UUID userUuid, Domain domain, Set<Scene> scenes) {
    ArrayList<Host> hosts = new ArrayList<>(scenes.size() + 2);

    hosts.addAll(scenes);

    // add global
    if (global.isSet(key)) {
      hosts.add(global);
    }

    // add domain
    if (domain.isSet(key)) {
      hosts.add(domain);
    }

    hosts.sort(Comparator.comparing(Host::priority));

    return key.extractValue(hosts, userUuid);
  }

  /**
   * Evaluate the result of a setting key for a specific user at a given block location.
   *
   * @param key      the key
   * @param userUuid the user's uuid
   * @param domain   the domain
   * @param x        the block x coordinate
   * @param y        the block y coordinate
   * @param z        the block z coordinate
   * @param <X>      the type of data to return
   * @return a record of the evaluation process
   */
  public <X> Evaluation<X> lookupBlock(@NotNull final SettingKey<X, ?, ?> key,
                                       @Nullable final UUID userUuid,
                                       Domain domain, int x, int y, int z) {
    Set<Scene> containingScenes = domain.volumes().containingBlock(x, y, z);
    return lookup(key, userUuid, domain, containingScenes);
  }

  /**
   * Evaluates a setting key, only considering the Global Host.
   *
   * @param key      the setting key
   * @param userUuid the uuid of the user
   * @param <X>      the result type
   * @return the evaluation
   */
  public <X> Evaluation<X> lookupGlobal(@NotNull final SettingKey<X, ?, ?> key,
                                        @Nullable final UUID userUuid) {

    ArrayList<Host> hosts = new ArrayList<>(1);

    // add global
    if (global.isSet(key)) {
      hosts.add(global);
    }

    return key.extractValue(hosts, userUuid);
  }

  /**
   * Implementation for the {@link SystemEditor}.
   */
  public static class Editor implements SystemEditor {

    @Override
    public HostEditor editGlobal() {
      return new Global.Editor();
    }

    @Override
    public Set<String> domains() {
      return Nope.instance().system().domains().realKeys();
    }

    @Override
    public HostEditor editDomain(String name) throws NoSuchElementException {
      Domain domain = Nope.instance().system().domains().get(name);
      if (domain == null) {
        throw new NoSuchElementException();
      }
      return new Domain.Editor(domain);
    }

    @Override
    public Set<String> scopes() {
      return Nope.instance().system().scopes().realKeys();
    }

    @Override
    public ScopeEditor editScope(String scopeName) {
      Scope scope = Nope.instance().system().scope(scopeName);
      if (scope == null) {
        throw new NoSuchElementException("No scope with name " + scopeName + " exists");
      }
      return new Scope.Editor(scope);
    }

    @Override
    public void registerScope(String scope) throws IllegalArgumentException {
      if (Validate.invalidId(scope)) {
        throw new IllegalArgumentException("The name " + scope + " is not a valid scope id");
      }
      Nope.instance().system().registerScope(scope);
    }
  }

}
