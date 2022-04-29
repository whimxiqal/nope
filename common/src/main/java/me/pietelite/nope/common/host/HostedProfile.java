package me.pietelite.nope.common.host;

import java.util.Optional;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import org.jetbrains.annotations.Nullable;

public class HostedProfile implements Targetable {

  private final Profile profile;
  private Target target;

  public HostedProfile(Profile profile) {
    this(profile, null);
  }

  public HostedProfile(Profile profile, Target target) {
    this.profile = profile;
    this.target = target;
  }

  public Profile profile() {
    return profile;
  }

  public Target target() {
    return target;
  }

  @Override
  public void target(@Nullable Target target) {
    this.target = target;
  }

  public Target activeTargetFor(SettingKey<?, ?, ?> key) {
    if (profile.getTarget(key).isPresent()) {
      return profile.getTarget(key).get();
    }
    if (target != null) {
      return target;
    }
    return profile.target();
  }

}
