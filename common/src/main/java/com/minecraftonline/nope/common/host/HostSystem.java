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

package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.struct.Location;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of HostTree making distinctions between
 * a GlobalHost, a WorldHost, and a Zone (VolumeHost).
 *
 * @see Host
 */
public class HostSystem {

  @Getter
  @Accessors(fluent = true)
  protected final Universe universe;

  /**
   * Domain id mapped to itself.
   */
  protected final HashMap<String, Domain> domains = new HashMap<>();

  protected final HashMap<String, Zone> zones = new HashMap<>();

  public HostSystem(Universe universe, Iterable<Domain> domains) {
    this.universe = universe;
    domains.forEach(domain -> this.domains.put(domain.name(), domain));
  }

  @NotNull
  public Map<String, Host> hosts() {
    Map<String, Host> hosts = new HashMap<>();
    hosts.put(this.universe.name(), this.universe);
    hosts.putAll(this.domains);
    hosts.putAll(this.zones);
    return hosts;
  }

  public Zone addZone(Zone zone) {
    Zone replaced = zones.put(zone.name().toLowerCase(), zone);
    Set<VolumeTree> trees = new HashSet<>();
    zone.volumes.forEach(volume -> {
      volume.domain().volumes().put(volume, zone, false);
      trees.add(volume.domain().volumes());
    });
    trees.forEach(VolumeTree::construct);
    return replaced;
  }

  public void addVolume(Volume volume, Zone zone) {
    zone.volumes.add(volume);
    volume.domain().volumes().put(volume, zone, true);
    zone.save();
  }

  public void addAllZones(Iterable<Zone> zones) {
    // Put all zones in the collection of zones for indexing by their name
    zones.forEach(zone -> this.zones.put(zone.name().toLowerCase(), zone));

    Set<VolumeTree> trees = new HashSet<>();
    // Add all volumes into volume tree
    zones.forEach(zone ->
        zone.volumes.forEach(volume -> {
          volume.domain().volumes().put(volume, zone, false);
          trees.add(volume.domain().volumes());
        }));
    // Construct all volume trees that were affected
    trees.forEach(VolumeTree::construct);
  }

  @Nullable
  public Zone removeZone(String zoneName) {
    Zone removed = zones.remove(zoneName.toLowerCase());
    if (removed != null) {
      Set<Domain> domains = new HashSet<>();
      removed.volumes.forEach(volume -> {
        volume.domain().volumes().remove(volume, false);
        domains.add(volume.domain());
      });
      removed.destroy();
      domains.forEach(domain -> domain.volumes().construct());
    }
    return removed;
  }

  public Volume removeVolume(Zone zone, int index) {
    Volume removed = zone.volumes.remove(index);
    removed.domain().volumes().remove(removed, true);
    return removed;
  }

  @Nullable
  public Zone get(String zoneName) {
    return zones.get(zoneName);
  }

  public boolean hasName(String hostName) {
    return host(hostName).isPresent();
  }

  public Collection<Zone> getAll() {
    return zones.values();
  }

  @NotNull
  public Set<Host> collectSuperiorHosts(@NotNull Location location) {
    Set<Host> set = new HashSet<>();
    set.add(universe);
    set.add(location.domain());

    // Add all the containing zones and their parents
    Set<Zone> zones = location.domain()
        .volumes()
        .containing(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    set.addAll(zones);
    accumulateParents(zones, set);
    return set;
  }

  private Set<Zone> containingZones(Zone zone, boolean discriminate) {
    Set<Zone> all = new HashSet<>();
    boolean first = true;
    // Only keep the zones which contain every single volume of the given zone
    for (Volume volume : zone.volumes) {
      if (first) {
        all.addAll(volume.domain().volumes().containing(volume, discriminate));
        first = false;
      } else {
        all.retainAll(volume.domain().volumes().containing(volume, discriminate));
      }
    }
    return all;
  }

  @NotNull
  public Set<Host> collectSuperiorHosts(Host host, boolean discriminate) {
    Set<Host> set = new HashSet<>();
    if (host instanceof Universe) {
      return set;  // Not contained by anything
    }
    set.add(universe);
    if (host instanceof Domain) {
      set.add(universe);  // Only contained by global
      return set;
    }
    if (!(host instanceof Zone)) {
      throw new IllegalArgumentException("The host of type " + host.getClass().getName() + " is unrecognized.");
    }
    // Add domains
    Zone zone = (Zone) host;
    zone.volumes.forEach(volume -> set.add(volume.domain()));

    // Add zones which contain this entire zone (all of its volumes)
    Set<Zone> containingZones = containingZones(zone, true);
    set.addAll(containingZones);

    // Grab all the parents of each containing zone
    accumulateParents(containingZones, set);
    return set;
  }

  private void accumulateParents(Set<Zone> zones, Set<Host> accumulator) {
    Zone current;
    for (Zone zone : zones) {
      current = zone;
      while (current.parent().isPresent() && !accumulator.contains(current.parent().get())) {
        current = zone.parent().get();
        accumulator.add(current);
      }
    }
  }

  public boolean isAssigned(SettingKey<?, ?, ?> key) {
    return hosts().values().stream().anyMatch(host -> host.get(key).isPresent());
  }

  public <X> X lookupAnonymous(@NotNull SettingKey<X, ?, ?> key,
                               @NotNull Location location) {
    return lookup(key, null, location);
  }

  public <X> X lookup(@NotNull final SettingKey<X, ?, ?> key,
                      @Nullable final UUID userUuid,
                      @NotNull final Location location) {
    LinkedList<Host> hosts = new LinkedList<>();

    // add universe
    if (universe.isSet(key)) {
      hosts.addFirst(universe);
    }

    // add domain
    if (location.domain().isSet(key)) {
      hosts.addFirst(location.domain());
    }

    // add zones
    location.domain()
        .volumes()
        .containing(location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ())
        .stream()
        .filter(zone -> zone.isSet(key))
        .forEach(hosts::addFirst);

    // add parents
    LinkedList<Host> unadded = hosts;
    hosts = new LinkedList<>();
    Host current;
    while (!unadded.isEmpty()) {
      current = unadded.pop();
      hosts.add(current);
      if (current instanceof Zone) {
        if (((Zone) current).parent().isPresent()) {
          unadded.add(((Zone) current).parent().get());
        }
      }
    }

    /*
    If setting data type is one piece of data, then try to find the zone with the highest
    priority with the setting set.

    If setting data type is a set of data, the build up the resulting value by going from the
    lowest priority to the highest priority.
     */

    // first we have to organize it such that we can match up the targets appropriately

    return key.extractValue(hosts, userUuid, location);

  }

  /**
   * Finds an immediate superior host which has the exact same data and target
   * applied with the given key. Returns empty if there is no superior host
   * with the given setting applied or if the most immediate superior (highest
   * in priority) has either a different data or target on the setting.
   * A superior host is one which is either a parent of the given host or
   * one which completely encapsulates the given host.
   *
   * @param host the host
   * @param key  the key
   * @return a superior with an identical setting
   */
  public Optional<Host> findIdenticalSuperior(Host host, SettingKey<?, ?, ?> key) {
    // Check if this host even has a setting
    Optional<? extends Setting<?, ?>> setting = host.get(key);
    if (!setting.isPresent()) {
      return Optional.empty();
    }
    List<Host> superiors = new LinkedList<>(collectSuperiorHosts(host, true));
    superiors.sort(Comparator.comparingInt(h -> -h.priority()));  // Sort maximum first
    for (Host superior : superiors) {
      // Continue if this superior doesn't have priority over the desired host
      if (superior.priority() > host.priority()) {
        continue;
      }

      Optional<? extends Setting<?, ?>> superiorSetting = superior.get(key);
      // Continue if the value is not found on this host
      if (!superiorSetting.isPresent()) {
        continue;
      }

      // If the value is the same, then its redundant. Otherwise, not redundant
      if (setting.get().equals(superiorSetting.get())) {
        return Optional.of(superior);
      } else {
        return Optional.empty();
      }
    }

    // We're out of superiors, so check if the default value is this one
    if (key.defaultData().equals(setting.get().value())) {
      // Return the original host to signify that the default value makes this redundant
      return Optional.of(host);
    } else {
      return Optional.empty();
    }
  }

  public Map<String, Domain> domains() {
    return new HashMap<>(domains);
  }

  @NotNull
  public Domain domain(String name) {
    Domain domain = domains.get(name.toLowerCase());
    if (domain == null) {
      throw new IllegalArgumentException("There is no domain called: " + name.toLowerCase());
    }
    return domain;
  }

  public Map<String, Zone> zones() {
    return new HashMap<>(zones);
  }

  public Zone zone(String name) {
    return zones.get(name);
  }

  public Optional<Host> host(String name) {
    if (name.equalsIgnoreCase(universe.name())) {
      return Optional.of(universe);
    } else if (domains.containsKey(name.toLowerCase())) {
      return Optional.of(domains.get(name.toLowerCase()));
    } else {
      return Optional.ofNullable(zones.get(name.toLowerCase()));
    }
  }
}
