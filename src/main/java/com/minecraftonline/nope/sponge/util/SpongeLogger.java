package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.sponge.SpongeNope;
import org.apache.logging.log4j.Logger;

public class SpongeLogger implements com.minecraftonline.nope.common.util.Logger {

  private Logger logger() {
    return SpongeNope.instance().getPluginContainer().logger();
  }

  @Override
  public void error(String string) {
    this.logger().error(string);
  }

  @Override
  public void warn(String string) {
    this.logger().warn(string);
  }

  @Override
  public void info(String string) {
    this.logger().info(string);
  }
}
