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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.Alteration;
import me.pietelite.nope.common.api.edit.AlterationImpl;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.SystemEditor;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HostSystem {

  private final HashMap<String, Domain> domains = new HashMap<>();
  private final HashMap<String, Scene> scenes = new HashMap<>();
  private final HashMap<String, Profile> profiles = new HashMap<>();
  private final Global global;

  /**
   * Generic constructor.
   *
   * @param global  the global
   * @param domains the domains
   */
  public HostSystem(Global global, Iterable<Domain> domains) {
    this.global = global;
    domains.forEach(domain -> this.domains.put(domain.name(), domain));
  }

  public Global global() {
    return global;
  }

  public HashMap<String, Domain> domains() {
    return domains;
  }

  public HashMap<String, Scene> scenes() {
    return scenes;
  }

  public HashMap<String, Profile> profiles() {
    return profiles;
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
    hosts.putAll(this.domains);
    hosts.putAll(this.scenes);
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
    scene.volumes.add(volume);
    volume.domain().volumes().put(volume, scene, true);
    scene.save();
  }

  /**
   * Add a series of scenes. This is faster than adding each one individually.
   *
   * @param scenes the scenes
   */
  public void addAllScenes(Iterable<Scene> scenes) {
    // Put all scenes in the collection of scenes for indexing by their name
    scenes.forEach(scene -> this.scenes.put(scene.name().toLowerCase(), scene));

    Set<VolumeTree> trees = new HashSet<>();
    // Add all volumes into volume tree
    scenes.forEach(scene ->
        scene.volumes.forEach(volume -> {
          volume.domain().volumes().put(volume, scene, false);
          trees.add(volume.domain().volumes());
        }));
    // Construct all volume trees that were affected
    trees.forEach(VolumeTree::construct);
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
      removed.volumes.forEach(volume -> {
        volume.domain().volumes().remove(volume, false);
        domains.add(volume.domain());
      });
      removed.markDestroyed();
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
   * that encapsulate that point and those hosts' parents.
   *
   * @param location the location for which to find superior hosts
   * @return the superior hosts
   */
  @NotNull
  public Set<Host> collectSuperiorHosts(@NotNull Location location) {
    Set<Host> set = new HashSet<>();
    set.add(global);
    set.add(location.domain());

    // Add all the containing scenes and their parents
    Set<Scene> scenes = location.domain()
        .volumes()
        .containing(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    set.addAll(scenes);
    return set;
  }

  /**
   * Get all the superior hosts around another {@link Host}.
   * A host is superior if it completely encapsulates the other or is an ancestor.
   *
   * @param host         the host for which to find other superior hosts
   * @param discriminate currently unused
   * @return the set of all superior hosts
   */
  @NotNull
  public Set<Host> collectSuperiorHosts(Host host, boolean discriminate) {
    Set<Host> set = new HashSet<>();
    if (host instanceof Global) {
      return set;  // Not contained by anything
    }
    set.add(global);
    if (host instanceof Domain) {
      set.add(global);  // Only contained by global
      return set;
    }
    if (!(host instanceof Scene)) {
      throw new IllegalArgumentException("The host of type "
          + host.getClass().getName()
          + " is unrecognized.");
    }
    // Add domains
    Scene scene = (Scene) host;
    scene.volumes.forEach(volume -> set.add(volume.domain()));

    // Add zones which contain this entire scene (all of its volumes)
    Set<Scene> containingScenes = containingZones(scene, true);
    set.addAll(containingScenes);

    return set;
  }

  private Set<Scene> containingZones(Scene scene, boolean discriminate) {
    Set<Scene> all = new HashSet<>();
    boolean first = true;
    // Only keep the zones which contain every single volume of the given scene
    for (Volume volume : scene.volumes) {
      if (first) {
        all.addAll(volume.domain().volumes().containing(volume, discriminate));
        first = false;
      } else {
        all.retainAll(volume.domain().volumes().containing(volume, discriminate));
      }
    }
    return all;
  }

  public boolean isAssigned(SettingKey<?, ?, ?> key) {
    return hosts().values().stream().anyMatch(host ->
        host.profiles().stream().anyMatch(profileItem ->
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
        .containing(location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ());

    ArrayList<Host> hosts = new ArrayList<>(containingScenes.size() + 2);

    hosts.addAll(containingScenes);

    // add global
    if (global.isSet(key)) {
      hosts.add(global);
    }

    // add domain
    if (location.domain().isSet(key)) {
      hosts.add(location.domain());
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

  public static class Editor implements SystemEditor {

    @Override
    public HostEditor editGlobal() {
      return new Global.Editor();
    }

    @Override
    public Set<String> domains() {
      return new HashSet<>(Nope.instance().system().domains.keySet());
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
      return new HashSet<>(Nope.instance().system().scenes.keySet());
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
    public Alteration createScene(String name, int priority) {
      Optional<Host> existingHost = Nope.instance().system().host(name);
      if (existingHost.isPresent()) {
        throw new IllegalArgumentException("A host already exists with the name " + existingHost.get().name());
      }
      Scene scene = new Scene(name, priority);
      Nope.instance().system().scenes().put(name.toLowerCase(), scene);
      scene.save();
      return AlterationImpl.success();
    }

    @Override
    public Set<String> profiles() {
      return new HashSet<>(Nope.instance().system().profiles.keySet());
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
    public Alteration createProfile(String name) {
      Profile existingProfile = Nope.instance().system().profiles().get(name.toLowerCase());
      if (existingProfile != null) {
        throw new IllegalArgumentException("A host already exists with the name " + existingProfile.name());
      }
      Profile profile = new Profile(name);
      Nope.instance().system().profiles().put(name.toLowerCase(), profile);
      profile.save();
      return AlterationImpl.success("Created profile " + name);
    }
  }

}
