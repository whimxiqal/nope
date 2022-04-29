package me.pietelite.nope.common.api.edit;

public interface SingleValueSettingEditor<T> extends SettingEditor {

  Alteration set(T value);

  T get();

}
