package me.pietelite.nope.common.api.edit;

import java.util.UUID;
import me.pietelite.nope.common.api.struct.AltSet;

public interface MultipleValueSettingEditor<T> extends SettingEditor {

  Alteration setAll();

  Alteration setNone();

  boolean isDeclarative();

  Alteration setDeclarative(AltSet<T> values);

  AltSet<T> getDeclarative();

  boolean isManipulative();

  Alteration setManipulative(AltSet<T> set, ManipulativeType type);

  AltSet<T> getManipulative(ManipulativeType type);

  enum ManipulativeType {
    ADDITIVE,
    SUBTRACTIVE
  }

}
