package minecraftonline.nope.control;

import com.google.common.collect.Lists;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * A library of methods to generate {@link Parameter}s for use in configuration.
 */
public final class Parameters {

  /**
   * Disable constructor.
   */
  private Parameters() {
  }

  public static final CatalogRegistryModule<Parameter<?>> REGISTRY_MODULE = new CatalogRegistryModule<Parameter<?>>() {
    @Nonnull
    @Override
    public Optional<Parameter<?>> getById(@Nonnull String id) {
      for (Parameter<?> griefEvent : getAll()) {
        if (griefEvent.getId().equalsIgnoreCase(id)) {
          return Optional.of(griefEvent);
        }
      }
      return Optional.empty();
    }

    @Override
    public Collection<Parameter<?>> getAll() {
      return Lists.newArrayList(
          ENABLE_PLUGIN,
          DEOP_ON_ENTER,
          LEAF_DECAY
          // TODO: add the rest
      );
    }
  };

  public static final Parameter<Boolean> ENABLE_PLUGIN = Parameter.of("general.enable", true, Boolean.class)
      .withDescription("Set to false will disable all plugin functionality");

  public static final Parameter<Boolean> DEOP_ON_ENTER = Parameter.of("security.deop-on-enter", false, Boolean.class)
      .withComment("Set to true will deop any player when they enter.")
      .withDescription("If this setting is applied globally, then anytime and op-ed player joins the server, their op status is removed. "
          + "If this setting is applied to just a world, then only when they join that specific world do they get de-opped.")
      .withApplicability(Parameter.Applicability.GLOBAL, Parameter.Applicability.WORLD);

  public static final Parameter<Boolean> LEAF_DECAY = Parameter.of("dynamics.leaf-decay", true, Boolean.class)
      .withDescription("Set to false will disable all natural leaf decay")
      .withApplicability(Parameter.Applicability.GLOBAL,
          Parameter.Applicability.WORLD,
          Parameter.Applicability.REGION);

  // TODO: add the rest

}
