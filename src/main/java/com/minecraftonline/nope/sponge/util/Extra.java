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
 */

package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;

/**
 * A utility class to store static methods for managing "extra"
 * miscellaneous behavior.
 */
public final class Extra {

  private Extra() {
  }

  /**
   * Sends a series of messages to Sponge's server console
   * notifying the user pleasantly that Nope is loaded.
   */
  public static void printSplashscreen() {
    TextComponent.Builder builder = Component.text();

    // Line 1
    builder.append(Component.text("           ").color(Formatter.WHITE));
    builder.append(Component.text("   ______    ").color(Formatter.ERROR));
    builder.append(Component.text(" ____    ").color(Formatter.WHITE));
    builder.append(Component.text(" ______").color(Formatter.WHITE));
    Sponge.server().sendMessage(builder.build());

    // Line 2
    builder = Component.text();
    builder.append(Component.text(" |\\      | ").color(Formatter.WHITE));
    builder.append(Component.text("  / ____ \\   ").color(Formatter.ERROR));
    builder.append(Component.text("|    \\   ").color(Formatter.WHITE));
    builder.append(Component.text("|").color(Formatter.WHITE));
    Sponge.server().sendMessage(builder.build());

    // Line 3
    builder = Component.text();
    builder.append(Component.text(" | \\     | ").color(Formatter.WHITE));
    builder.append(Component.text(" //\\\\    \\\\  ").color(Formatter.ERROR));
    builder.append(Component.text("|     \\  ").color(Formatter.WHITE));
    builder.append(Component.text("|      ").color(Formatter.WHITE));
    Sponge.server().sendMessage(builder.build());

    // Line 4
    builder = Component.text();
    builder.append(Component.text(" |  \\    | ").color(Formatter.WHITE));
    builder.append(Component.text("||  \\\\    || ").color(Formatter.ERROR));
    builder.append(Component.text("|     /  ").color(Formatter.WHITE));
    builder.append(Component.text("|____  ").color(Formatter.WHITE));
    builder.append(Component.text("   for ").color(Formatter.DULL));
    builder.append(Component.text("MinecraftOnline").color(Formatter.THEME));
    Sponge.server().sendMessage(builder.build());

    // Line 5
    builder = Component.text();
    builder.append(Component.text(" |   \\   | ").color(Formatter.WHITE));
    builder.append(Component.text("||   \\\\   || ").color(Formatter.ERROR));
    builder.append(Component.text("|____/   ").color(Formatter.WHITE));
    builder.append(Component.text("|      ").color(Formatter.WHITE));
    builder.append(Component.text("   by  ").color(Formatter.DULL));
    builder.append(Component.text("Pieter Svenson").color(Formatter.DULL));
    Sponge.server().sendMessage(builder.build());

    // Line 6
    builder = Component.text();
    builder.append(Component.text(" |    \\  | ").color(Formatter.WHITE));
    builder.append(Component.text("||    \\\\  || ").color(Formatter.ERROR));
    builder.append(Component.text("|        ").color(Formatter.WHITE));
    builder.append(Component.text("|      ").color(Formatter.WHITE));
    builder.append(Component.text("      v" + SpongeNope.instance().pluginContainer().metadata().version())
        .color(Formatter.GOLD));
    Sponge.server().sendMessage(builder.build());

    // Line 7
    builder = Component.text();
    builder.append(Component.text(" |     \\ | ").color(Formatter.WHITE));
    builder.append(Component.text(" \\\\____\\\\//  ").color(Formatter.ERROR));
    builder.append(Component.text("|        ").color(Formatter.WHITE));
    builder.append(Component.text("|").color(Formatter.WHITE));
    Sponge.server().sendMessage(builder.build());

    // Line 8
    builder = Component.text();
    builder.append(Component.text(" |      \\| ").color(Formatter.WHITE));
    builder.append(Component.text("  \\______/   ").color(Formatter.ERROR));
    builder.append(Component.text("|        ").color(Formatter.WHITE));
    builder.append(Component.text("|______").color(Formatter.WHITE));
    Sponge.server().sendMessage(builder.build());

  }

  /**
   * Generates a {@link RuntimeException} for not being able to find
   * the location during an event while looking for a setting with
   * a given setting key and player.
   *
   * @param key        the setting key
   * @param eventClass the type of event
   * @param player     the player which is in question. Null if anonymous.
   * @return a supplier for the runtime exception
   */
  public static Supplier<RuntimeException> noLocation(SettingKey<?, ?, ?> key,
                                                      Class<? extends Event> eventClass,
                                                      @Nullable Player player) {
    return () -> new RuntimeException(String.format(
        "The relevant location for the dynamic event listener for "
            + "Setting Key %s and event class %s could not be found.",
        key.id(),
        eventClass.getName())
        +
        (player == null
            ? ""
            : String.format(" The player is %s at position (%d, %d, %d) in world %s",
            player.name(),
            player.location().blockX(),
            player.location().blockY(),
            player.location().blockZ(),
            player.world())));
  }
}
