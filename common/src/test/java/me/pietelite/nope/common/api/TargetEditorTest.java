/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.api;

import java.util.UUID;
import java.util.function.Supplier;
import me.pietelite.nope.common.api.edit.TargetEditor;
import org.junit.jupiter.api.Assertions;

public class TargetEditorTest {

  static void editTarget(TargetEditor targetEditor, Supplier<Boolean> hasTarget) {
    targetEditor.remove();
    Assertions.assertFalse(hasTarget.get());
    targetEditor.targetAll();
    Assertions.assertTrue(hasTarget.get());
    Assertions.assertTrue(targetEditor.playerSet().isEmpty());
    Assertions.assertEquals(TargetEditor.Type.BLACKLIST, targetEditor.targetType());
    Assertions.assertFalse(targetEditor.targetType(TargetEditor.Type.BLACKLIST));
    Assertions.assertTrue(targetEditor.targetType(TargetEditor.Type.WHITELIST));
    Assertions.assertTrue(targetEditor.targetType(TargetEditor.Type.BLACKLIST));
    Assertions.assertTrue(targetEditor.playerSet().isEmpty());
    Assertions.assertTrue(targetEditor.permissions().isEmpty());
    Assertions.assertFalse(targetEditor.clearPlayers());
    Assertions.assertFalse(targetEditor.clearPermissions());

    targetEditor.targetNone();
    Assertions.assertTrue(hasTarget.get());
    Assertions.assertTrue(targetEditor.playerSet().isEmpty());
    Assertions.assertEquals(TargetEditor.Type.WHITELIST, targetEditor.targetType());
    Assertions.assertFalse(targetEditor.targetType(TargetEditor.Type.WHITELIST));
    Assertions.assertTrue(targetEditor.targetType(TargetEditor.Type.BLACKLIST));
    Assertions.assertTrue(targetEditor.targetType(TargetEditor.Type.WHITELIST));
    Assertions.assertTrue(targetEditor.playerSet().isEmpty());
    Assertions.assertTrue(targetEditor.permissions().isEmpty());
    Assertions.assertFalse(targetEditor.clearPlayers());
    Assertions.assertFalse(targetEditor.clearPermissions());

    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    Assertions.assertTrue(targetEditor.addPlayer(player1));
    Assertions.assertFalse(targetEditor.addPlayer(player1));
    Assertions.assertTrue(targetEditor.playerSet().contains(player1));
    Assertions.assertFalse(targetEditor.playerSet().contains(player2));

    Assertions.assertTrue(targetEditor.addPlayer(player2));
    Assertions.assertFalse(targetEditor.addPlayer(player2));
    Assertions.assertTrue(targetEditor.playerSet().contains(player1));
    Assertions.assertTrue(targetEditor.playerSet().contains(player2));
    Assertions.assertTrue(targetEditor.clearPlayers());
    Assertions.assertFalse(targetEditor.clearPlayers());

    String permission1 = "perm.1";
    Assertions.assertTrue(targetEditor.addPermission(permission1, true));
    Assertions.assertFalse(targetEditor.addPermission(permission1, true));
    Assertions.assertTrue(targetEditor.permissions().containsKey(permission1));
    Assertions.assertTrue(targetEditor.permissions().get(permission1));
    Assertions.assertTrue(targetEditor.addPermission(permission1, false));
    Assertions.assertFalse(targetEditor.addPermission(permission1, false));
    Assertions.assertTrue(targetEditor.permissions().containsKey(permission1));
    Assertions.assertFalse(targetEditor.permissions().get(permission1));

    Assertions.assertFalse(targetEditor.bypassUnrestricted());
    Assertions.assertFalse(targetEditor.bypassUnrestricted(false));
    Assertions.assertTrue(targetEditor.bypassUnrestricted(true));
    Assertions.assertFalse(targetEditor.bypassUnrestricted(true));
    Assertions.assertTrue(targetEditor.bypassUnrestricted());
    Assertions.assertFalse(targetEditor.bypassUnrestricted(true));
    Assertions.assertTrue(targetEditor.bypassUnrestricted(false));
    Assertions.assertFalse(targetEditor.bypassUnrestricted(false));
    Assertions.assertFalse(targetEditor.bypassUnrestricted());
  }

}
