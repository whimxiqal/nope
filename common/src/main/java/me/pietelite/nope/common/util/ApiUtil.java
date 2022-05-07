package me.pietelite.nope.common.util;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.NopeServiceProvider;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.ScopeEditor;

public final class ApiUtil {

  public static ScopeEditor editNopeScope() {
    return NopeServiceProvider.service().editSystem().editScope(Nope.NOPE_SCOPE);
  }

  public static HostEditor editHost(String name) {
    if (name.equals(Nope.GLOBAL_ID)) {
      return NopeServiceProvider.service().editSystem().editGlobal();
    }
    if (Nope.instance().system().domains().containsKey(name)) {
      return NopeServiceProvider.service().editSystem().editDomain(name);
    }
    return editNopeScope().editScene(name);
  }

}
