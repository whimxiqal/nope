///*
// *
// *  * MIT License
// *  *
// *  * Copyright (c) 2021 Pieter Svenson
// *  *
// *  * Permission is hereby granted, free of charge, to any person obtaining a copy
// *  * of this software and associated documentation files (the "Software"), to deal
// *  * in the Software without restriction, including without limitation the rights
// *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// *  * copies of the Software, and to permit persons to whom the Software is
// *  * furnished to do so, subject to the following conditions:
// *  *
// *  * The above copyright notice and this permission notice shall be included in all
// *  * copies or substantial portions of the Software.
// *  *
// *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// *  * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.sponge.listenerold.dynamic;
//
//import com.minecraftonline.nope.common.setting.SettingKeys;
//import com.minecraftonline.nope.sponge.listenerold.PlayeredCancelerSettingListener;
//import com.minecraftonline.nope.sponge.util.SpongeUtil;
//import org.spongepowered.api.data.type.HandTypes;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.block.InteractBlockEvent;
//import org.spongepowered.api.item.ItemTypes;
//import org.spongepowered.api.world.server.ServerLocation;
//
//public class ArmorStandPlaceListener extends PlayeredCancelerSettingListener<InteractBlockEvent.Secondary> {
//  public ArmorStandPlaceListener() {
//    super(InteractBlockEvent.Secondary.class, SettingKeys.ARMOR_STAND_PLACE);
//  }
//
//  @Override
//  public boolean shouldCancel(InteractBlockEvent.Secondary event, Player player) {
//    return player.itemInHand(HandTypes.MAIN_HAND).type().equals(ItemTypes.ARMOR_STAND.get())
//        && !SpongeUtil.valueFor(SettingKeys.ARMOR_STAND_PLACE, player, player.serverLocation())
//        && !SpongeUtil.valueFor(SettingKeys.ARMOR_STAND_PLACE, player, ServerLocation.of(
//        player.serverLocation().world(), event.interactionPoint()));
//  }
//}
