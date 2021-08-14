package com.minecraftonline.nope.common.setting.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class TemplateSet {

  private final HashMap<String, Template> map = new HashMap<>();

  public Collection<Template> getAll() {
    return map.values();
  }

  public Template add(Template template) {
    return map.put(template.name(), template);
  }

  public Template get(String name) {
    return map.get(name);
  }

}
