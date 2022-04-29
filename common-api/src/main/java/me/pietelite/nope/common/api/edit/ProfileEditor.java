package me.pietelite.nope.common.api.edit;

public interface ProfileEditor {

  String name();

  Alteration name(String name) throws IllegalArgumentException;

  TargetEditor editTarget();

  <T> SingleValueSettingEditor<T> editSingleValueSetting(String setting, Class<T> type);

  <T> MultipleValueSettingEditor<T> editMultipleValueSetting(String setting, Class<T> type);

  Alteration destroy();

}
