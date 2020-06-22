package minecraftonline.nope;

import com.google.inject.Inject;
import minecraftonline.nope.util.Reference;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = Reference.ID,
    name = Reference.NAME,
    description = Reference.DESCRIPTION,
    url = Reference.URL,
    authors = {"PietElite", "tyhdefu", "14mRh4X0r"},
    version = Reference.VERSION
)
public class Nope {

  @Inject
  private Logger logger;

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
  }
}
