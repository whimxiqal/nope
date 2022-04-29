package me.pietelite.nope.common.api.edit;

import java.util.NoSuchElementException;
import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;

public class MultipleValueSettingEditorImpl<T> extends SettingEditorImpl implements MultipleValueSettingEditor<T> {

  private final Class<T> type;

  public MultipleValueSettingEditorImpl(Profile profile, String setting, Class<T> type) {
    super(profile, setting);
    this.type = type;
  }

  private SettingKey.Poly<T, ?> key() {
    profile.verifyExistence();
    return Nope.instance().settingKeys().getPolySetting(setting, type);
  }

  @Override
  public Alteration setAll() {
    setAllGeneric(key());
    profile.save();
    return AlterationImpl.success("Updated value of " + setting + " on profile " + profile.name());
  }

  private <X, Y extends AltSet<X>> void setAllGeneric(SettingKey.Poly<X, Y> key) {
    profile.setValue(key, SettingValue.Poly.declarative(key.manager().fullSet()));
  }

  @Override
  public Alteration setNone() {
    setNoneGeneric(key());
    profile.save();
    return AlterationImpl.success("Updated value of " + setting + " on profile " + profile.name());
  }

  private <X, Y extends AltSet<X>> void setNoneGeneric(SettingKey.Poly<X, Y> key) {
    profile.setValue(key, SettingValue.Poly.declarative(key.manager().emptySet()));
  }

  @Override
  public boolean isDeclarative() {
    return profile.getValue(key()).orElseThrow(NoSuchElementException::new).declarative();
  }

  @Override
  public Alteration setDeclarative(AltSet<T> values) {
    setDeclarativeGeneric(key(), values);
    profile.save();
    return AlterationImpl.success("Updated value of " + setting + " on profile " + profile.name());
  }

  private <X, Y extends AltSet<X>> void setDeclarativeGeneric(SettingKey.Poly<X, Y> key, AltSet<X> values) {
    Y newSet = key.manager().emptySet();
    newSet.addAll(values);
    profile.setValue(key, SettingValue.Poly.declarative(newSet));
  }

  @Override
  public AltSet<T> getDeclarative() {
    SettingKey.Poly<T, ?> key = key();
    AltSet<T> out = key.manager().emptySet();
    SettingValue.Poly<T, ?> value = profile.getValue(key).orElseThrow(NoSuchElementException::new);
    if (!value.declarative()) {
      throw new NoSuchElementException();
    }
    out.addAll(value.additive());
    return out;
  }

  @Override
  public boolean isManipulative() {
    return profile.getValue(key()).orElseThrow(NoSuchElementException::new).manipulative();
  }

  @Override
  public Alteration setManipulative(AltSet<T> set, ManipulativeType manipulativeType) {
    setManipulativeGeneric(key(), set, manipulativeType);
    profile.save();
    return AlterationImpl.success("Updated value of " + setting + " on profile " + profile.name());
  }

  private <X, Y extends AltSet<X>> void setManipulativeGeneric(SettingKey.Poly<X, Y> key,
                                                               AltSet<X> set,
                                                               ManipulativeType manipulativeType) {
    Optional<SettingValue.Poly<X, Y>> value = profile.getValue(key);
    switch (manipulativeType) {
      case ADDITIVE:
        Y newAdditive = key.manager().emptySet();
        newAdditive.addAll(set);
        if (value.isPresent() && value.get().manipulative()) {
          profile.setValue(key, SettingValue.Poly.manipulative(newAdditive,
              key.manager().copySet(value.get().subtractive())));
        } else {
          profile.setValue(key, SettingValue.Poly.manipulative(newAdditive, key.manager().emptySet()));
        }
        break;
      case SUBTRACTIVE:
        Y newSubtractive = key.manager().emptySet();
        newSubtractive.addAll(set);
        if (value.isPresent() && value.get().manipulative()) {
          profile.setValue(key, SettingValue.Poly.manipulative(
              key.manager().copySet(value.get().additive()),
              newSubtractive));
        } else {
          profile.setValue(key, SettingValue.Poly.manipulative(
              key.manager().emptySet(),
              newSubtractive));
        }
        break;
      default:
        throw new RuntimeException();
    }
  }

  @Override
  public AltSet<T> getManipulative(ManipulativeType manipulativeType) {
    SettingKey.Poly<T, ?> key = key();
    AltSet<T> out = key.manager().emptySet();
    SettingValue.Poly<T, ?> value = profile.getValue(key).orElseThrow(NoSuchElementException::new);
    if (!value.manipulative()) {
      throw new NoSuchElementException();
    }
    switch (manipulativeType) {
      case ADDITIVE:
        out.addAll(value.additive());
        break;
      case SUBTRACTIVE:
        out.addAll(value.subtractive());
        break;
      default:
        throw new RuntimeException();
    }
    return out;
  }

}
