package com.minecraftonline.nope.sponge.command.general.arguments;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.WandCommand;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.sponge.util.Format;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

/**
 * A {@link CommandElement} to identify a zone selection. The player may either
 * specify the zone selection as a command argument or may make a selection
 * with a zone wand.
 */
public class ZoneLocationSelectionCommandElement extends CommandElement {
  private final Vector3iCommandElement vector3i = new Vector3iCommandElement(Text.of("vector3i"));

  protected ZoneLocationSelectionCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nonnull
  @Override
  public Text getUsage(@Nonnull CommandSource src) {
    return Text.of("[[-w <world>] ", vector3i.getUsage(src), "]");
  }

  @Nullable
  @Override
  protected ZoneWandHandler.Selection parseValue(@Nonnull CommandSource source,
                                                 CommandArgs args) throws ArgumentParseException {
    if (!args.hasNext()) {
      if (source instanceof Player) {
        Player player = (Player) source;
        ZoneWandHandler.Selection selection = SpongeNope.getInstance()
            .getZoneWandHandler()
            .getSelectionMap()
            .get(player.getUniqueId());
        if (selection == null || !selection.isComplete()) {
          throw new ArgumentParseException(Format.error("Make a selection first using the ",
              Format.command(
                  "wand",
                  SpongeNope.getInstance().getCommandTree()
                      .findNode(WandCommand.class)
                      .orElseThrow(() ->
                          new RuntimeException("Wand command is not present in the Nope command tree!"))
                      .getFullCommand(),
                  Text.of("Get a zone wand"))), "", 0);
        }
        return selection;
      } else {
        throw new ArgumentParseException(
            Format.error("You must provide co-ordinates if you are not a player!"),
            "",
            0);
      }
    }

    String first = args.peek();
    final World world;
    if (first.equals("-w") || first.equals("--world")) {
      // World flag
      args.next();
      String worldName = args.next();
      world = Sponge.getServer().getWorlds().stream()
          .filter(w -> w.getName().equals(worldName))
          .findAny()
          .orElseThrow(() -> new ArgumentParseException(Text.of("Invalid world name"),
              worldName,
              first.length() + 1));
    } else if (source instanceof Player) {
      world = ((Player) source).getWorld();
    } else {
      throw new ArgumentParseException(
          Text.of("Could not infer world. Please provide it with \"-w <world name>\""),
          first,
          0);
    }

    Vector3i min = vector3i.parseValue(source, args);
    Vector3i max = vector3i.parseValue(source, args);

    for (Vector3i vec : new Vector3i[]{min, max}) {
      for (int pos : new int[]{vec.getX(), vec.getZ()}) {
        if (pos < -SpongeNope.WORLD_RADIUS) {
          throw new ArgumentParseException(Text.of("Value " + pos + " is too small!"),
              String.valueOf(pos),
              0);
        }
        if (pos > SpongeNope.WORLD_RADIUS) {
          throw new ArgumentParseException(Text.of("Value " + pos + " is too large!"),
              String.valueOf(pos),
              0);
        }
      }
      if (vec.getY() < -SpongeNope.WORLD_DEPTH) {
        throw new ArgumentParseException(Text.of("Value " + vec.getY() + " is too small!"),
            String.valueOf(vec.getY()),
            0);
      }
      if (vec.getY() > SpongeNope.WORLD_DEPTH) {
        throw new ArgumentParseException(Text.of("Value " + vec.getY() + " is too large!"),
            String.valueOf(vec.getY()),
            0);
      }
    }

    return new ZoneWandHandler.Selection(world, min, max);
  }

  @Nonnull
  @Override
  public List<String> complete(@Nonnull CommandSource src,
                               @Nonnull CommandArgs args,
                               @Nonnull CommandContext context) {
    return Collections.emptyList();
  }
}
