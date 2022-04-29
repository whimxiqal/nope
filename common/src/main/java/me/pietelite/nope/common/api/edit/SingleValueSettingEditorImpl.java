package me.pietelite.nope.common.api.edit;

import java.util.NoSuchElementException;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import org.jetbrains.annotations.Nullable;

public class SingleValueSettingEditorImpl<T> extends SettingEditorImpl implements SingleValueSettingEditor<T> {

  private final Class<T> type;

  public SingleValueSettingEditorImpl(Profile profile, String setting, Class<T> type) {
    super(profile, setting);
    this.type = type;
  }

  @Override
  public Alteration set(T value) {
    profile.verifyExistence();
    SettingKey.Unary<T> key = Nope.instance().settingKeys().getUnarySetting(setting, type);
    profile.setValue(key, SettingValue.Unary.of(value));
    return AlterationImpl.success("Set value of "
        + setting + " on profile "
        + profile.name() + " to "
        + key.manager().printData(value));
  }

  @Override
  public T get() {
    profile.verifyExistence();
    SettingKey.Unary<T> key = Nope.instance().settingKeys().getUnarySetting(setting, type);
    return profile.getValue(key).map(SettingValue.Unary::get).orElseThrow(NoSuchElementException::new);
  }

}
