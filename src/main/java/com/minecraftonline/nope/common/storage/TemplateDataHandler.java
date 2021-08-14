package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.setting.template.Template;
import java.util.Collection;

public interface TemplateDataHandler {

  void save(Collection<Template> templates);

  Collection<Template> load();

}
