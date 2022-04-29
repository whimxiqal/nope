package me.pietelite.nope.common.api.edit;

public interface SettingEditor {

  boolean hasValue();

  Alteration unsetValue();

  boolean hasTarget();

  TargetEditor editTarget();

}
