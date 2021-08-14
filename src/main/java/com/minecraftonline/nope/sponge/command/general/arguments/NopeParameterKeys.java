package com.minecraftonline.nope.sponge.command.general.arguments;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.sponge.wand.Selection;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.command.parameter.Parameter;

public class NopeParameterKeys {

  public static final Parameter.Key<Host<?>> HOST = Parameter.key("host", new TypeToken<Host<?>>(){});
  public static final Parameter.Key<Template> TEMPLATE = Parameter.key("template", Template.class);
  public static final Parameter.Key<Selection> SELECTION = Parameter.key("selection", Selection.class);
  public static final Parameter.Key<Integer> PRIORITY = Parameter.key("priority", Integer.class);
  public static final Parameter.Key<String> NAME = Parameter.key("name", String.class);
  public static final Parameter.Key<String> REGEX = Parameter.key("regex", String.class);

}
