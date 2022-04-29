package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import org.jetbrains.annotations.Nullable;

public abstract class SettingEditorImpl implements SettingEditor {

  protected final Profile profile;
  protected final String setting;

  public SettingEditorImpl(Profile profile, String setting) {
    this.profile = profile;
    this.setting = setting;
  }


  @Override
  public boolean hasValue() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return profile.getValue(key).isPresent();
  }

  @Override
  public Alteration unsetValue() {
    profile.verifyExistence();
    return AlterationImpl.success("Unset value of " + setting + " on profile " + profile.name());
  }

  @Override
  public TargetEditor editTarget() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return new Target.Editor(new Targetable() {
      @Override
      public @Nullable Target target() {
        return profile.getTarget(key).orElse(null);
      }

      @Override
      public void target(@Nullable Target target) {
        if (target == null) {
          profile.removeTarget(key);
        } else {
          profile.setTarget(key, target);
        }
      }
    }, profile::save);
  }

  @Override
  public boolean hasTarget() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return profile.getTarget(key).isPresent();
  }
}
