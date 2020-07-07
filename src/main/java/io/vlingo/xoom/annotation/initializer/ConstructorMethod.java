// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.XoomInitializationAware;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.*;
import static javax.lang.model.element.Modifier.PUBLIC;

public class ConstructorMethod {

    private final Elements elements;
    private final String basePackage;
    private final Xoom xoomAnnotation;
    private final TypeElement bootstrapClass;

    private ConstructorMethod(final String basePackage,
                              final Elements elements,
                              final TypeElement bootstrapClass) {
        this.elements = elements;
        this.basePackage = basePackage;
        this.bootstrapClass = bootstrapClass;
        this.xoomAnnotation = bootstrapClass.getAnnotation(Xoom.class);
    }

    public static MethodSpec from(final String basePackage,
                                  final ProcessingEnvironment environment,
                                  final Element bootstrapClass) {
        return new ConstructorMethod(basePackage, environment.getElementUtils(),
                (TypeElement) bootstrapClass).build();
    }

    private MethodSpec build() {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        final String blockingMailboxStatement =
                xoomAnnotation.blocking() ? BLOCKING_MAILBOX_ENABLING_STATEMENT :
                        BLOCKING_MAILBOX_DISABLING_STATEMENT;

        final Object[] initializationAwareClass = resolveInitializationAwareClass();
        final Map.Entry<String, Object[]> resourcesStatement = resolveResourcesStatement();
        final Map.Entry<String, Object[]> stageInstanceStatement = resolveStageInstanceStatement();

        return MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC).addParameter(String[].class, "args")
                .addStatement(blockingMailboxStatement, Settings.class)
                .addStatement(WORLD_INSTANCE_STATEMENT, World.class, xoomAnnotation.name(), Settings.class)
                .addStatement(stageInstanceStatement.getKey(), stageInstanceStatement.getValue())
                .addStatement(INITIALIZER_INSTANTIATION_STATEMENT, initializationAwareClass)
                .addStatement(ON_INIT_STATEMENT).addStatement(SERVER_CONFIGURATION_STATEMENT, Configuration.class)
                .addStatement(resourcesStatement.getKey(), resourcesStatement.getValue())
                .addStatement(SERVER_INSTANCE_STATEMENT, Server.class)
                .addStatement(SHUTDOWN_HOOK_STATEMENT, xoomAnnotation.name())
                .build();
    }

    private Object[] resolveInitializationAwareClass() {
        final TypeMirror stageInitializerAwareInterface =
                elements.getTypeElement(XoomInitializationAware.class.getCanonicalName()).asType();

        final Object initializerClass =
                bootstrapClass.getInterfaces().contains(stageInitializerAwareInterface) ?
                        bootstrapClass : ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME);

        return new Object[]{initializerClass, initializerClass};
    }

    private Map.Entry<String, Object[]> resolveStageInstanceStatement() {
        if(xoomAnnotation.addressFactory().type().isBasic()) {
            return new AbstractMap.SimpleEntry(BASIC_STAGE_INSTANCE_STATEMENT,
                    new Object[]{Stage.class, xoomAnnotation.name()});
        }

        final AddressFactory.IdentityGenerator generator =
                xoomAnnotation.addressFactory().generator();

        final IdentityGeneratorType identityGeneratorType =
                generator.resolveWith(xoomAnnotation.addressFactory().type());

        return new AbstractMap.SimpleEntry(STAGE_INSTANCE_STATEMENT,
                new Object[]{Stage.class, xoomAnnotation.name(), Stage.class,
                        xoomAnnotation.addressFactory().type().clazz,
                        IdentityGeneratorType.class, identityGeneratorType});
    }

    private Map.Entry<String, Object[]> resolveResourcesStatement() {
        final Object[] defaultParameters = new Object[]{Resources.class, Resources.class};

        final ResourceHandlers resourceHandlersAnnotation =
                bootstrapClass.getAnnotation(ResourceHandlers.class);

        if(resourceHandlersAnnotation == null) {
            return new AbstractMap.SimpleEntry(String.format(RESOURCES_STATEMENT_PATTERN, ""), defaultParameters);
        }

        final Object[] endpointClasses = retrieveResourcesClasses(resourceHandlersAnnotation);

        final String routesStatement =
                Stream.of(endpointClasses).map(clazz -> ROUTES_STATEMENT)
                        .collect(Collectors.joining(", "));

        return new AbstractMap.SimpleEntry(String.format(RESOURCES_STATEMENT_PATTERN, routesStatement),
                ArrayUtils.addAll(defaultParameters, endpointClasses));
    }

    private Object[] retrieveResourcesClasses(final ResourceHandlers resourceHandlersAnnotation) {
        try {
            return resourceHandlersAnnotation.value();
        } catch (final MirroredTypesException exception) {
            return exception.getTypeMirrors().toArray();
        }
    }
}
