/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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
//import org.spongepowered.api.entity.EntityTypes;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.entity.InteractEntityEvent;
//
//public class ArmorStandInteractListener extends PlayeredCancelerSettingListener<InteractEntityEvent.Secondary> {
//
//  public ArmorStandInteractListener() {
//    super(InteractEntityEvent.Secondary.class, SettingKeys.ARMOR_STAND_DESTROY);
//  }
//
//  @Override
//  public boolean shouldCancel(InteractEntityEvent.Secondary event, Player player) {
//    return event.entity().type().equals(EntityTypes.ARMOR_STAND.get())
//        && !SpongeUtil.valueFor(SettingKeys.ARMOR_STAND_INTERACT,
//        player,
//        event.entity().serverLocation());
//  }
//
//}
