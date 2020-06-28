/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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
 *
 */

package minecraftonline.nope.control;

import com.google.common.collect.Lists;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * A library of methods to generate {@link Setting}s for use in configuration.
 */
public final class Settings {

  /**
   * Disable constructor.
   */
  private Settings() {
  }

  public static final CatalogRegistryModule<Setting<?>> REGISTRY_MODULE = new CatalogRegistryModule<Setting<?>>() {
    @Nonnull
    @Override
    public Optional<Setting<?>> getById(@Nonnull String id) {
      for (Setting<?> griefEvent : getAll()) {
        if (griefEvent.getId().equalsIgnoreCase(id)) {
          return Optional.of(griefEvent);
        }
      }
      return Optional.empty();
    }

    @Override
    public Collection<Setting<?>> getAll() {
      return Lists.newArrayList(
          ENABLE_PLUGIN,
          DEOP_ON_ENTER,
          LEAF_DECAY
          // TODO: add more
      );
    }
  };

  public static final Setting<Boolean> ENABLE_PLUGIN = Setting.of("enable-plugin", true, Boolean.class)
      .withDescription("Set to false will disable all plugin functionality")
      .withConfigurationPath("general.enable-plugin");

  public static final Setting<Boolean> DEOP_ON_ENTER = Setting.of("deop-on-enter", false, Boolean.class)
      .withComment("Set to true will deop any player when they enter.")
      .withDescription("If this setting is applied globally, then anytime and op-ed player joins the server, their op status is removed. "
          + "If this setting is applied to just a world, then only when they join that specific world do they get de-opped.")
      .withApplicability(Setting.Applicability.GLOBAL, Setting.Applicability.WORLD)
      .withConfigurationPath("security.deop-on-enter");

  public static final Setting<Boolean> LEAF_DECAY = Setting.of("leaf-decay", true, Boolean.class)
      .withDescription("Set to false will disable all natural leaf decay")
      .withApplicability(Setting.Applicability.GLOBAL,
          Setting.Applicability.WORLD,
          Setting.Applicability.REGION)
      .withConfigurationPath("dynamics.leaf-decay");

  // TODO: add more

}
