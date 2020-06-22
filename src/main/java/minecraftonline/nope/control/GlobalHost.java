package minecraftonline.nope.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;

public class GlobalHost implements Host {

  private Set<Setting<?>> settings = Sets.newHashSet();

  public GlobalHost(Set<Setting<?>> settings) {
    Preconditions.checkNotNull(settings);
    this.settings.addAll(settings);
  }

  public void addSetting(Setting<?> setting) {
    this.settings.add(setting);
  }

  @Override
  public Set<Setting<?>> getSettings() {
    return settings;
  }
}
