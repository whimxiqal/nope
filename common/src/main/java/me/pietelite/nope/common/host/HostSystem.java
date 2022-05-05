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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.SystemEditor;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.IgnoreCaseStringHashMap;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HostSystem {

  private final IgnoreCaseStringHashMap<Domain> domains = new IgnoreCaseStringHashMap<>();
  private final IgnoreCaseStringHashMap<Scene> scenes = new IgnoreCaseStringHashMap<>();
  private final IgnoreCaseStringHashMap<Profile> profiles = new IgnoreCaseStringHashMap<>();
  private final Map<String, Set<Host>> profileBackwardsMap = new HashMap<>();
  private Global global;

  public Global global() {
    return global;
  }

  public void global(Global global) {
    this.global = global;
  }

  public IgnoreCaseStringHashMap<Domain> domains() {
    return domains;
  }

  public IgnoreCaseStringHashMap<Scene> scenes() {
    return scenes;
  }

  public IgnoreCaseStringHashMap<Profile> profiles() {
    return profiles;
  }

  public void relateProfile(String profileName, Host scene) {
    profileBackwardsMap.computeIfAbsent(profileName, k -> new HashSet<>()).add(scene);
  }

  public void unrelateProfile(String profileName) {
    profileBackwardsMap.remove(profileName);
  }

  public Set<Host> relatedToProfile(String profileName) {
    return profileBackwardsMap.get(profileName);
  }

  /**
   * Returns a map of every host keyed by its name.
   *
   * @return all hosts
   */
  @NotNull
  public Map<String, Host> hosts() {
    Map<String, Host> hosts = new HashMap<>();
    hosts.put(this.global.name(), this.global);
    hosts.putAll(this.domains.map());
    hosts.putAll(this.scenes.map());
    return hosts;
  }

  /**
   * Add a volume into a scene and updates the internal structures
   * to be aware of the scene's increased ownership of a domain.
   *
   * @param volume the volume to add on the scene
   * @param scene  the scene
   */
  public void addVolume(Volume volume, Scene scene) {
    LinkedList<Volume> newVolumes = new LinkedList<>();
    for (Volume oldVolume : scene.volumes()) {
      if (oldVolume.uuid().equals(volume.uuid())) {
        assert oldVolume.domain() == volume.domain();
        newVolumes.add(volume);
        volume.domain().volumes().remove(oldVolume, false);
      } else {
        newVolumes.add(oldVolume);
      }
    }
    scene.volumes(newVolumes);
    volume.domain().volumes().put(volume, scene, true);
    ensureScenePriority(scene);
    scene.save();
  }

  /**
   * Load a series of scenes into the system.
   *
   * @param scenes the scenes
   */
  public void loadScenes(Iterable<Scene> scenes) {
    // Put all scenes in the collection of scenes for indexing by their name
    scenes.forEach(scene -> this.scenes.put(scene.name().toLowerCase(), scene));

    Set<Domain> domains = new HashSet<>();
    // Add all volumes into volume tree
    scenes.forEach(scene -> scene.volumes().forEach(volume -> {
      volume.domain().volumes().put(volume, scene, false);
      domains.add(volume.domain());
    }));
    // Construct all volume trees that were affected
    domains.forEach(domain -> domain.volumes().construct());
  }

  /**
   * Remove a {@link Scene} from the system by name.
   *
   * @param sceneName the name of the zone
   * @return the {@link Scene} that was removed
   */
  @Nullable
  public Scene removeScene(String sceneName) {
    Scene removed = scenes.remove(sceneName.toLowerCase());
    if (removed != null) {
      Set<Domain> domains = new HashSet<>();
      removed.volumes().forEach(volume -> {
        volume.domain().volumes().remove(volume, false);
        domains.add(volume.domain());
      });
      removed.expire();
      domains.forEach(domain -> domain.volumes().construct());
    }
    return removed;
  }

  @Nullable
  public Scene get(String sceneName) {
    return scenes.get(sceneName);
  }

  public Collection<Scene> getAll() {
    return scenes.values();
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

  public boolean isAssigned(SettingKey<?, ?, ?> key) {
    return hosts().values().stream().anyMatch(host ->
        host.hostedProfiles().stream().anyMatch(profileItem ->
            profileItem.profile().get(key).isPresent()));
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
   * Lookup a host by name.
   *
   * @param name the name of a host
   * @return the host, or an empty optional if none exists
   */
  public Optional<Host> host(String name) {
    if (name.equalsIgnoreCase(global.name())) {
      return Optional.of(global);
    } else if (domains.containsKey(name.toLowerCase())) {
      return Optional.of(domains.get(name.toLowerCase()));
    } else {
      return Optional.ofNullable(scenes.get(name.toLowerCase()));
    }
  }

  public void updateScenePriority(Scene scene, int newPriority, UpdatePrioritiesResult result) {
    if (newPriority < 0) {
      throw new IllegalArgumentException("Cannot set a negative priority");
    }
    if (newPriority >= Integer.MAX_VALUE) {
      result.failChangedCount++;
      return;
    }
    if (scene.priority != newPriority) {
      result.successfullyChangedCount++;
    }
    scene.priority = newPriority;
    scene.save();
    scene.volumes().forEach(volume -> volume.domain().volumes()
        .intersecting(scene)
        .stream()
        .filter(other -> !other.equals(scene))
        .filter(other -> scene.priority() == other.priority())
        .forEach(zone -> updateScenePriority(zone, scene.priority + 1, result)));
  }

  public void ensureScenePriority(Scene scene) {
    updateScenePriority(scene, scene.priority, new HostSystem.UpdatePrioritiesResult());
  }

  public static class UpdatePrioritiesResult {
    int successfullyChangedCount;
    int failChangedCount;
  }

  public static class Editor implements SystemEditor {

    @Override
    public HostEditor editHost(String name) {
      Scene scene = Nope.instance().system().scenes().get(name);
      if (scene != null) {
        return new Scene.Editor(scene);
      }
      Domain domain = Nope.instance().system().domains().get(name);
      if (domain != null) {
        return new Domain.Editor(domain);
      }
      if (name.equalsIgnoreCase(Nope.GLOBAL_ID)) {
        return new Global.Editor();
      }
      throw new NoSuchElementException();
    }

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
    public Set<String> scenes() {
      return Nope.instance().system().scenes().realKeys();
    }

    @Override
    public SceneEditor editScene(String name) throws NoSuchElementException {
      Scene scene = Nope.instance().system().scenes().get(name);
      if (scene == null) {
        throw new NoSuchElementException();
      }
      return new Scene.Editor(scene);
    }

    @Override
    public void createScene(String name, int priority) {
      Optional<Host> existingHost = Nope.instance().system().host(name);
      if (existingHost.isPresent()) {
        throw new IllegalArgumentException("A host already exists with the name " + existingHost.get().name());
      }
      Scene scene = new Scene(name, priority);
      Nope.instance().system().scenes().put(name.toLowerCase(), scene);
      scene.save();
    }

    @Override
    public Set<String> profiles() {
      return Nope.instance().system().profiles().realKeys();
    }

    @Override
    public ProfileEditor editProfile(String name) throws NoSuchElementException {
      Profile profile = Nope.instance().system().profiles().get(name.toLowerCase());
      if (profile == null) {
        throw new NoSuchElementException("There is no profile with name: " + name);
      }
      return new Profile.Editor(profile);
    }

    @Override
    public void createProfile(String name) {
      Profile existingProfile = Nope.instance().system().profiles().get(name.toLowerCase());
      if (existingProfile != null) {
        throw new IllegalArgumentException("A host already exists with the name " + existingProfile.name());
      }
      Profile profile = new Profile(name);
      Nope.instance().system().profiles().put(name.toLowerCase(), profile);
      profile.save();
    }
  }

}
