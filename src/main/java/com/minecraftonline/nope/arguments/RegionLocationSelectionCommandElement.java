package com.minecraftonline.nope.arguments;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.RegionWandHandler;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RegionLocationSelectionCommandElement extends CommandElement {
  private final Vector3iCommandElement vector3i = new Vector3iCommandElement(Text.of("vector3i"));

  protected RegionLocationSelectionCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected RegionWandHandler.Selection parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    if (args.hasNext()) {
      String first = args.next();
      final World world;
      if (first.equals("-w") || first.equals("--world")) {
        // World flag
        String worldName = args.next();
        world = Sponge.getServer().getWorlds().stream()
            .filter(w -> w.getName().equals(worldName))
            .findAny()
            .orElseThrow(() -> new ArgumentParseException(Text.of("Invalid world name"), worldName, worldName.length()));
      } else if (source instanceof Player) {
        world = ((Player) source).getWorld();
      } else {
        throw new ArgumentParseException(Text.of("Could not infer world. Please provide it with -w worldname"), first, 0);
      }
      Vector3i min = vector3i.parseValue(source, args);
      Vector3i max = vector3i.parseValue(source, args);
      return new RegionWandHandler.Selection(world, min, max);
    } else if (source instanceof Player) {
      Player player = (Player) source;
      RegionWandHandler.Selection selection = Nope.getInstance().getRegionWandHandler().getSelectionMap().get(player);
      if (selection == null || !selection.isComplete()) {
        throw new ArgumentParseException(Format.error("Make a selection first using the ",
            Format.command(
                "wand",
                "/nope region wand",
                Text.of("Get a region wand"))), "", 0);
      }
      return selection;
    } else {
      throw new ArgumentParseException(Format.error("You must provide co-ordinates if you are not a player!"), "", 0);
    }
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    return Collections.emptyList();
  }
}
