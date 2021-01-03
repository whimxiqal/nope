///*
// * MIT License
// *
// * Copyright (c) 2020 MinecraftOnline
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.minecraftonline.nope.listener.flag;
//
//import com.minecraftonline.nope.Nope;
//import com.minecraftonline.nope.control.flags.FlagString;
//import com.minecraftonline.nope.control.flags.Membership;
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.Listener;
//import org.spongepowered.api.event.command.SendCommandEvent;
//import org.spongepowered.api.text.serializer.TextSerializers;
//
//public class CommandListener extends FlagListener {
//  @Listener
//  public void onCommand(SendCommandEvent e) {
//    CommandSource source = (CommandSource) e.getSource();
//    if (!(source instanceof Player)) {
//      return;
//    }
//    Player player = (Player) source;
//    if (Nope.getInstance().canOverrideRegion(player)) {
//      return; // Force allow
//    }
//    Membership membership = Membership.player(player);
//    RegionSet regionSet = Nope.getInstance().getGlobalHost().getRegions(player.getLocation());
//    regionSet.findFirstFlagSetting(Settings.FLAG_BLOCKED_COMMANDS, membership).ifPresent(value -> {
//      if (value.getValue().contains(e.getCommand())) {
//        e.setCancelled(true);
//
//        regionSet.findFirstFlagSetting(Settings.FLAG_COMMAND_DENY_MESSAGE, membership)
//            .map(FlagString::getValue)
//            .map(TextSerializers.FORMATTING_CODE::deserialize)
//            .filter(text -> !text.isEmpty())
//            .ifPresent(player::sendMessage);
//      }
//    });
//  }
//}
