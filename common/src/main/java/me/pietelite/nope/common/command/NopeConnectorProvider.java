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

import me.pietelite.mantle.common.connector.CommandConnector;
import me.pietelite.mantle.common.connector.CompletionInfo;
import me.pietelite.mantle.common.connector.HelpCommandInfo;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.NopeLexer;
import me.pietelite.nope.common.NopeParser;
import me.pietelite.nope.common.message.Formatter;

public final class NopeConnectorProvider {

  public static CommandConnector connector() {
    return CommandConnector.builder()
        .setBaseCommand("nope")
        .setLexerClass(NopeLexer.class)
        .setParserClass(NopeParser.class)
        .setExecutionHandler(new NopeExecutor())
        .setHelpCommandInfo(HelpCommandInfo.builder()
            .addDescription(NopeParser.RULE_nope, Formatter.accent("Manage all ___ assets", "nope"))
            .addIgnored(NopeParser.RULE_identifier)
            .build())
        .setCompletionInfo(CompletionInfo.builder()
            .addParameter("host", () -> Nope.instance().system()
                .hosts(Nope.NOPE_SCOPE)
                .keySet())
            .addParameter("profile", () -> Nope.instance().system()
                .scope(Nope.NOPE_SCOPE)
                .profiles()
                .realKeys())
            .addParameter("setting", () -> Nope.instance().settingKeys().keys().keySet())
            .registerCompletion(NopeParser.RULE_host, NopeParser.RULE_identifier, 0, "host")
            .registerCompletion(NopeParser.RULE_profile, NopeParser.RULE_identifier, 0, "profile")
            .registerCompletion(NopeParser.RULE_evaluate, NopeParser.RULE_identifier, 0, "profile")
            .registerCompletion(NopeParser.RULE_profileEditSingleSetting,
                NopeParser.RULE_identifier,
                0,
                "profile")
            .registerCompletion(NopeParser.RULE_profileEditMultiSetting,
                NopeParser.RULE_identifier,
                0,
                "profile")
            .addIgnoredCompletionToken(NopeParser.SINGLE_QUOTE)
            .addIgnoredCompletionToken(NopeParser.DOUBLE_QUOTE)
            .build())
        .build();
  }

}
