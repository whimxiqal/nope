package com.minecraftonline.nope;

import com.minecraftonline.nope.control.Setting;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigPathNode {
  private String key;
  @Nullable
  private Setting<?> setting = null;
  private Map<String, ConfigPathNode> children = new HashMap<>();

  public ConfigPathNode(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void addChild(String[] path, Setting<?> setting) {
    addChild(path, 0, setting);
  }

  public void addChild(String[] path, int cur, Setting<?> setting) {
    if (cur >= path.length) {
      this.setting = setting;
      return; // Already got there.
    }
    this.children.compute(path[cur], (k,v) -> {
      if (k == null || v == null) {
        v = new ConfigPathNode(path[cur]);
      }
      v.addChild(path, cur + 1, setting);
      return v;
    });
  }

  public Map<String, ConfigPathNode> getChildren() {
    return children;
  }

  public Optional<Setting<?>> getSetting() {
    return Optional.ofNullable(setting);
  }

  public List<ConfigPathNode> getChildlessChildren() {
    return this.children.values().stream()
        .filter(configPathNode -> configPathNode.getChildren().isEmpty())
        .collect(Collectors.toList());
  }

  public List<ConfigPathNode> getChildrenWithChildren() {
    return this.children.values().stream()
        .filter(configPathNode -> !configPathNode.getChildren().isEmpty())
        .collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfigPathNode that = (ConfigPathNode) o;
    return key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }
}
