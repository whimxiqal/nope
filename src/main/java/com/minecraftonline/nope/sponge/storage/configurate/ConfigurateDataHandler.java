package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.HostSystem;
import com.minecraftonline.nope.common.host.Universe;
import com.minecraftonline.nope.common.storage.DataHandler;
import com.minecraftonline.nope.common.storage.DomainDataHandler;
import com.minecraftonline.nope.common.storage.TemplateDataHandler;
import com.minecraftonline.nope.common.storage.UniverseDataHandler;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;

@AllArgsConstructor
public abstract class ConfigurateDataHandler implements DataHandler {

  private final UniverseConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final ZoneConfigurateDataHandler zoneDataHandler;
  private final TemplateConfigurateDataHandler templateConfigurateDataHandler;

  @Override
  public UniverseDataHandler universe() {
    return universeDataHandler;
  }

  @Override
  public DomainDataHandler domains() {
    return domainDataHandler;
  }

  @Override
  public ZoneDataHandler zones() {
    return zoneDataHandler;
  }

  @Override
  public TemplateDataHandler templates() {
    return templateConfigurateDataHandler;
  }

  public HostSystem loadSystem() {
    Universe universe = universeDataHandler.load();
    List<Domain> domains = Sponge.server()
        .worldManager()
        .worlds()
        .stream()
        .map(world -> new Domain(world.properties()
            .displayName()
            .map(name -> PlainTextComponentSerializer.plainText().serialize(name))
            .orElse(world.key().formatted()),
            world.key().formatted(),
            universe))
        .collect(Collectors.toList());
    domains.forEach(domainDataHandler::load);
    HostSystem hostSystem = new HostSystem(universe, domains);
    loadZones(hostSystem);
    return hostSystem;
  }

}
