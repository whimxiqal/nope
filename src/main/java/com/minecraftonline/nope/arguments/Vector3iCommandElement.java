package com.minecraftonline.nope.arguments;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Vector3iCommandElement extends CommandElement {
    protected Vector3iCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    public Vector3i parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String first;
        first = args.next();

        Supplier<String> all;
        String[] parts;

        if (first.contains(",")) {
            all = () -> first;
            parts = first.split(",", 3);
        }
        else {
            parts = new String[] {first, args.next(), args.next()};
            all = () -> String.join(" ", parts);
        }

        int i = 0;
        try {
            int x = Integer.parseInt(parts[i++]);
            int y = Integer.parseInt(parts[i++]);
            int z = Integer.parseInt(parts[i]);
            return Vector3i.from(x,y,z);
        } catch (NumberFormatException e) {
            // Could have been joined with spaces but that doesn't matter for the length
            int upToFailedArg = String.join(",", Arrays.copyOfRange(parts, 0, i)).length();
            throw new ArgumentParseException(Text.of("Could not parse integer no." + i), all.get(), upToFailedArg);
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }
}
