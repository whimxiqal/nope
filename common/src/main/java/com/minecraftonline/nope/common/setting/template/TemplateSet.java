/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.common.setting.template;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.template.Template;
import java.util.Collection;
import java.util.HashMap;

public class TemplateSet {

  private final HashMap<String, Template> map = new HashMap<>();

  public Collection<Template> templates() {
    return map.values();
  }

  public Template add(Template template) {
    Template replaced = map.put(template.name(), template);
    Nope.instance().data().templates().save(this);
    return replaced;
  }

  public Template remove(String name) {
    Template removed = map.remove(name);
    if (removed != null) {
      Nope.instance().data().templates().save(this);
    }
    return removed;
  }

  public Template get(String name) {
    return map.get(name);
  }

}
