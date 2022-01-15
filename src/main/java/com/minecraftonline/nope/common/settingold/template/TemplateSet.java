//package com.minecraftonline.nope.common.settingold.template;
//
//import com.minecraftonline.nope.common.Nope;
//import java.util.Collection;
//import java.util.HashMap;
//
//public class TemplateSet {
//
//  private final HashMap<String, Template> map = new HashMap<>();
//
//  public Collection<Template> templates() {
//    return map.values();
//  }
//
//  public Template add(Template template) {
//    Template replaced = map.put(template.name(), template);
//    Nope.instance().data().templates().save(this);
//    return replaced;
//  }
//
//  public Template remove(String name) {
//    Template removed = map.remove(name);
//    if (removed != null) {
//      Nope.instance().data().templates().save(this);
//    }
//    return removed;
//  }
//
//  public Template get(String name) {
//    return map.get(name);
//  }
//
//}
