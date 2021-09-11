package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.setting.template.TemplateSet;

public interface TemplateDataHandler {

  void save(TemplateSet templates);

  TemplateSet load();

}
