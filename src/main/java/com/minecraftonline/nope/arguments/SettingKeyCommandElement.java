package com.minecraftonline.nope.arguments;

import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SettingKeyCommandElement extends CommandElement {

    protected SettingKeyCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String s = args.next();
        try {
            return SettingLibrary.lookup(s);
        } catch (NoSuchElementException e) {
            throw new ArgumentParseException(Text.of("Unknown setting: " + s), s, 0);
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String beginning = args.nextIfPresent().orElse("");
        List<String> completions = SettingLibrary.getAll().stream()
                .filter(s -> s.startsWith(beginning))
                .collect(Collectors.toList());
        return completions;
    }
}
