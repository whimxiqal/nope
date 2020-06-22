package minecraftonline.nope.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class WorldHost implements Host {

  private Set<Setting<?>> settings = Sets.newHashSet();
  private UUID worldUuid;

  public WorldHost(@Nonnull Set<Setting<?>> settings,
                   @Nonnull UUID worldUuid) {
    Preconditions.checkNotNull(settings);
    Preconditions.checkNotNull(worldUuid);
    this.settings.addAll(settings);
    this.worldUuid = worldUuid;
  }

  public void addSetting(Setting<?> setting) {
    this.settings.add(setting);
  }

  public UUID getWorldUuid() {
    return this.worldUuid;
  }

  @Override
  public Set<Setting<?>> getSettings() {
    return settings;
  }

}
