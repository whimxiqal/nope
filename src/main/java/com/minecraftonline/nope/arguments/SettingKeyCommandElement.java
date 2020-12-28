package com.minecraftonline.nope.arguments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

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
        final String prefix = args.nextIfPresent().orElse("");

        return SettingLibrary.getAll().stream()
                .filter(new StartsWithPredicate(prefix))
                .collect(ImmutableList.toImmutableList());
    }
}
