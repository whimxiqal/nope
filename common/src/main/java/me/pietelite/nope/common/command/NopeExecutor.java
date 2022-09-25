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
 *
 */

package me.pietelite.nope.common.command;

import java.util.Locale;
import me.pietelite.mantle.common.Mantle;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.NopeBaseVisitor;
import me.pietelite.nope.common.NopeParser;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;

public class NopeExecutor extends NopeBaseVisitor<Boolean> {

  public SettingKey<?, ?, ?> settingKey(NopeParser.IdentifierContext identifierContext) {
    String key = identifierContext.getText().toLowerCase(Locale.ROOT);
    if (!Nope.instance().settingKeys().containsId(key)) {
      return null;
    }
    return Nope.instance().settingKeys().get(identifierContext.getText().toLowerCase(Locale.ROOT));
  }

  @Override
  public Boolean visitNope(NopeParser.NopeContext ctx) {
    if (ctx.children.isEmpty()) {
      Mantle.session().getSource().getAudience().sendMessage(Identity.nil(), Formatter.WELCOME);
    }
    return true;
  }

  @Override
  public Boolean visitEvaluate(NopeParser.EvaluateContext ctx) {
    SettingKey<?, ?, ?> key = settingKey(ctx.setting);
    return super.visitEvaluate(ctx);
  }

  @Override
  public Boolean visitHost(NopeParser.HostContext ctx) {
    return super.visitHost(ctx);
  }

  @Override
  public Boolean visitHostDestroy(NopeParser.HostDestroyContext ctx) {
    return super.visitHostDestroy(ctx);
  }

  @Override
  public Boolean visitHostEdit(NopeParser.HostEditContext ctx) {
    return super.visitHostEdit(ctx);
  }

  @Override
  public Boolean visitHostEditName(NopeParser.HostEditNameContext ctx) {
    return super.visitHostEditName(ctx);
  }

  @Override
  public Boolean visitHostEditPriority(NopeParser.HostEditPriorityContext ctx) {
    return super.visitHostEditPriority(ctx);
  }

  @Override
  public Boolean visitHostEditProfiles(NopeParser.HostEditProfilesContext ctx) {
    return super.visitHostEditProfiles(ctx);
  }

  @Override
  public Boolean visitHostEditTarget(NopeParser.HostEditTargetContext ctx) {
    return super.visitHostEditTarget(ctx);
  }

  @Override
  public Boolean visitHostEditZones(NopeParser.HostEditZonesContext ctx) {
    return super.visitHostEditZones(ctx);
  }

  @Override
  public Boolean visitCreateZone(NopeParser.CreateZoneContext ctx) {
    return super.visitCreateZone(ctx);
  }

  @Override
  public Boolean visitCreateZoneBuilder(NopeParser.CreateZoneBuilderContext ctx) {
    return super.visitCreateZoneBuilder(ctx);
  }

  @Override
  public Boolean visitCreateZoneExplicit(NopeParser.CreateZoneExplicitContext ctx) {
    return super.visitCreateZoneExplicit(ctx);
  }

  @Override
  public Boolean visitCreateBox(NopeParser.CreateBoxContext ctx) {
    return super.visitCreateBox(ctx);
  }

  @Override
  public Boolean visitCreateCylinder(NopeParser.CreateCylinderContext ctx) {
    return super.visitCreateCylinder(ctx);
  }

  @Override
  public Boolean visitCreateSlab(NopeParser.CreateSlabContext ctx) {
    return super.visitCreateSlab(ctx);
  }

  @Override
  public Boolean visitCreateSphere(NopeParser.CreateSphereContext ctx) {
    return super.visitCreateSphere(ctx);
  }

  @Override
  public Boolean visitCreateZoneSelection(NopeParser.CreateZoneSelectionContext ctx) {
    return super.visitCreateZoneSelection(ctx);
  }

  @Override
  public Boolean visitDestroyZone(NopeParser.DestroyZoneContext ctx) {
    return super.visitDestroyZone(ctx);
  }

  @Override
  public Boolean visitEditZone(NopeParser.EditZoneContext ctx) {
    return super.visitEditZone(ctx);
  }

  @Override
  public Boolean visitInsertProfile(NopeParser.InsertProfileContext ctx) {
    return super.visitInsertProfile(ctx);
  }

  @Override
  public Boolean visitRemoveProfile(NopeParser.RemoveProfileContext ctx) {
    return super.visitRemoveProfile(ctx);
  }

  @Override
  public Boolean visitHostInfo(NopeParser.HostInfoContext ctx) {
    return super.visitHostInfo(ctx);
  }

  @Override
  public Boolean visitHostShow(NopeParser.HostShowContext ctx) {
    return super.visitHostShow(ctx);
  }

  @Override
  public Boolean visitHosts(NopeParser.HostsContext ctx) {
    return super.visitHosts(ctx);
  }

  @Override
  public Boolean visitHostsCreate(NopeParser.HostsCreateContext ctx) {
    return super.visitHostsCreate(ctx);
  }

  @Override
  public Boolean visitProfile(NopeParser.ProfileContext ctx) {
    return super.visitProfile(ctx);
  }

  @Override
  public Boolean visitProfileDestroy(NopeParser.ProfileDestroyContext ctx) {
    return super.visitProfileDestroy(ctx);
  }

  @Override
  public Boolean visitProfileEdit(NopeParser.ProfileEditContext ctx) {
    return super.visitProfileEdit(ctx);
  }

  @Override
  public Boolean visitProfileEditClear(NopeParser.ProfileEditClearContext ctx) {
    return super.visitProfileEditClear(ctx);
  }

  @Override
  public Boolean visitProfileEditName(NopeParser.ProfileEditNameContext ctx) {
    return super.visitProfileEditName(ctx);
  }

  @Override
  public Boolean visitProfileEditSetting(NopeParser.ProfileEditSettingContext ctx) {
    return super.visitProfileEditSetting(ctx);
  }

  @Override
  public Boolean visitProfileEditSingleSetting(NopeParser.ProfileEditSingleSettingContext ctx) {
    return super.visitProfileEditSingleSetting(ctx);
  }

  @Override
  public Boolean visitAllSettingValue(NopeParser.AllSettingValueContext ctx) {
    return super.visitAllSettingValue(ctx);
  }

  @Override
  public Boolean visitSingleSettingValue(NopeParser.SingleSettingValueContext ctx) {
    return super.visitSingleSettingValue(ctx);
  }

  @Override
  public Boolean visitSingleSet(NopeParser.SingleSetContext ctx) {
    return super.visitSingleSet(ctx);
  }

  @Override
  public Boolean visitProfileEditMultiSetting(NopeParser.ProfileEditMultiSettingContext ctx) {
    return super.visitProfileEditMultiSetting(ctx);
  }

  @Override
  public Boolean visitMultiSettingValue(NopeParser.MultiSettingValueContext ctx) {
    return super.visitMultiSettingValue(ctx);
  }

  @Override
  public Boolean visitMultiAdd(NopeParser.MultiAddContext ctx) {
    return super.visitMultiAdd(ctx);
  }

  @Override
  public Boolean visitMultiRemove(NopeParser.MultiRemoveContext ctx) {
    return super.visitMultiRemove(ctx);
  }

  @Override
  public Boolean visitMultiSet(NopeParser.MultiSetContext ctx) {
    return super.visitMultiSet(ctx);
  }

  @Override
  public Boolean visitMultiSetAll(NopeParser.MultiSetAllContext ctx) {
    return super.visitMultiSetAll(ctx);
  }

  @Override
  public Boolean visitMultiSetNone(NopeParser.MultiSetNoneContext ctx) {
    return super.visitMultiSetNone(ctx);
  }

  @Override
  public Boolean visitMultiSetNot(NopeParser.MultiSetNotContext ctx) {
    return super.visitMultiSetNot(ctx);
  }

  @Override
  public Boolean visitProfileEditor(NopeParser.ProfileEditorContext ctx) {
    return super.visitProfileEditor(ctx);
  }

  @Override
  public Boolean visitProfileInfo(NopeParser.ProfileInfoContext ctx) {
    return super.visitProfileInfo(ctx);
  }

  @Override
  public Boolean visitProfiles(NopeParser.ProfilesContext ctx) {
    return super.visitProfiles(ctx);
  }

  @Override
  public Boolean visitProfilesCreate(NopeParser.ProfilesCreateContext ctx) {
    return super.visitProfilesCreate(ctx);
  }

  @Override
  public Boolean visitReload(NopeParser.ReloadContext ctx) {
    return super.visitReload(ctx);
  }

  @Override
  public Boolean visitSettings(NopeParser.SettingsContext ctx) {
    return super.visitSettings(ctx);
  }

  @Override
  public Boolean visitTool(NopeParser.ToolContext ctx) {
    return super.visitTool(ctx);
  }

  @Override
  public Boolean visitVerbose(NopeParser.VerboseContext ctx) {
    return super.visitVerbose(ctx);
  }

  @Override
  public Boolean visitZones(NopeParser.ZonesContext ctx) {
    return super.visitZones(ctx);
  }

  @Override
  public Boolean visitTarget(NopeParser.TargetContext ctx) {
    return super.visitTarget(ctx);
  }

  @Override
  public Boolean visitTargetForce(NopeParser.TargetForceContext ctx) {
    return super.visitTargetForce(ctx);
  }

  @Override
  public Boolean visitTargetPermission(NopeParser.TargetPermissionContext ctx) {
    return super.visitTargetPermission(ctx);
  }

  @Override
  public Boolean visitTargetPermissionAdd(NopeParser.TargetPermissionAddContext ctx) {
    return super.visitTargetPermissionAdd(ctx);
  }

  @Override
  public Boolean visitTargetPermissionClear(NopeParser.TargetPermissionClearContext ctx) {
    return super.visitTargetPermissionClear(ctx);
  }

  @Override
  public Boolean visitTargetPermissionRemove(NopeParser.TargetPermissionRemoveContext ctx) {
    return super.visitTargetPermissionRemove(ctx);
  }

  @Override
  public Boolean visitTargetSubject(NopeParser.TargetSubjectContext ctx) {
    return super.visitTargetSubject(ctx);
  }

  @Override
  public Boolean visitTargetSubjectAdd(NopeParser.TargetSubjectAddContext ctx) {
    return super.visitTargetSubjectAdd(ctx);
  }

  @Override
  public Boolean visitTargetSubjectClear(NopeParser.TargetSubjectClearContext ctx) {
    return super.visitTargetSubjectClear(ctx);
  }

  @Override
  public Boolean visitTargetSubjectRemove(NopeParser.TargetSubjectRemoveContext ctx) {
    return super.visitTargetSubjectRemove(ctx);
  }

  @Override
  public Boolean visitTargetSet(NopeParser.TargetSetContext ctx) {
    return super.visitTargetSet(ctx);
  }

  @Override
  public Boolean visitTargetToggle(NopeParser.TargetToggleContext ctx) {
    return super.visitTargetToggle(ctx);
  }

  @Override
  public Boolean visitZone(NopeParser.ZoneContext ctx) {
    return super.visitZone(ctx);
  }

  @Override
  public Boolean visitIdentifier(NopeParser.IdentifierContext ctx) {
    return super.visitIdentifier(ctx);
  }
}
