// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import java.lang.annotation.Annotation;

public class XoomInitializerStatements {

    public static final String ROUTES_STATEMENT = "new $T(stage).routes()";
    public static final String ON_INIT_STATEMENT = "initializer.onInit(stage)";
    public static final String RESOURCES_STATEMENT_PATTERN = "final $T resources = $T.are(%s)";
    public static final String BLOCKING_MAILBOX_ENABLING_STATEMENT = "$T.enableBlockingMailbox()";
    public static final String BLOCKING_MAILBOX_DISABLING_STATEMENT = "$T.disableBlockingMailbox()";
    public static final String WORLD_INSTANCE_STATEMENT = "world = $T.start($S, $T.properties())";
    public static final String BASIC_STAGE_INSTANCE_STATEMENT = "final $T stage = world.stageNamed($S)";
    public static final String INITIALIZER_INSTANTIATION_STATEMENT = "final $T initializer = new $T()";
    public static final String STAGE_INSTANCE_STATEMENT = "final $T stage = world.stageNamed($S, $T.class, new $T($T.$L))";
    public static final String SERVER_CONFIGURATION_STATEMENT = "final $T serverConfiguration = initializer.configureServer(stage, args)";

    public static final String SERVER_INSTANCE_STATEMENT = "server = $T.startWith(stage, resources, " +
            "serverConfiguration.port(), serverConfiguration.sizing(), serverConfiguration.timing())";

    public static final String INITIALIZER_INSTANCE_STATEMENT = "System.out.println(\"=========================\");\n" +
            "System.out.println(\"service: $L.\");\n" +
            "System.out.println(\"=========================\");\n" +
            "instance = new XoomInitializer(args)";

    public static final String SHUTDOWN_HOOK_STATEMENT = "Runtime.getRuntime().addShutdownHook(new Thread(() -> { \n" +
            " if (instance != null) { \n" +
            "instance.server.stop(); \n" +
            "System.out.println(\"=========================\");\n" +
            "System.out.println(\"Stopping $L.\");\n" +
            "System.out.println(\"=========================\");\n" +
            "} \n" +
            "}))";

}