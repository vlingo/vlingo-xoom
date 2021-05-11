// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.template.initializer;

import io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.template.Label;
import io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter;
import io.vlingo.xoom.turbo.annotation.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.StorageType;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.*;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.BLOCKING_MESSAGING;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.PROJECTION_TYPE;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.STORAGE_TYPE;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.*;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.APPLICATION_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.REST_RESOURCES;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.USE_ANNOTATIONS;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.*;

public class XoomInitializerTemplateData extends TemplateData {

  private final TemplateParameters parameters;

  public XoomInitializerTemplateData(final CodeGenerationContext context) {
    final List<Content> contents = context.contents();
    final String packageName = context.parameterOf(PACKAGE);
    final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
    final String xoomInitializerClass = context.parameterOf(XOOM_INITIALIZER_NAME);
    final Boolean hasExchange = ContentQuery.exists(EXCHANGE_BOOTSTRAP, context.contents());
    final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::valueOf);
    final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
    final Boolean blockingMessaging = context.parameterOf(BLOCKING_MESSAGING, Boolean::valueOf);
    final Boolean customInitialization = !xoomInitializerClass.equals(XOOM_INITIALIZER.resolveClassname());

    this.parameters = TemplateParameters.with(TemplateParameter.BLOCKING_MESSAGING, blockingMessaging)
            .and(APPLICATION_NAME, context.parameterOf(Label.APPLICATION_NAME)).and(PACKAGE_NAME, packageName)
            .and(PROVIDERS, StoreProvider.from(storageType, useCQRS, projectionType.isProjectionEnabled(), hasExchange))
            .and(USE_ANNOTATIONS, context.parameterOf(Label.USE_ANNOTATIONS, Boolean::valueOf))
            .andResolve(PROJECTION_DISPATCHER_PROVIDER_NAME, PROJECTION_DISPATCHER_PROVIDER::resolveClassname)
            .and(XOOM_INITIALIZER_CLASS, context.parameterOf(XOOM_INITIALIZER_NAME))
            .and(EXCHANGE_BOOTSTRAP_NAME, resolveExchangeBootstrapName(context))
            .and(TYPE_REGISTRIES, TypeRegistry.from(storageType, useCQRS))
            .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
            .and(CUSTOM_INITIALIZATION, customInitialization)
            .and(REST_RESOURCES, RestResource.from(contents))
            .addImports(resolveImports(context));
  }

  private Set<String> resolveImports(final CodeGenerationContext context) {
    final Boolean useCQRS =
            context.parameterOf(CQRS, Boolean::valueOf);

    final StorageType storageType =
            context.parameterOf(STORAGE_TYPE, StorageType::valueOf);

    final TemplateStandard[] dependencies =
            new TemplateStandard[]{STORE_PROVIDER, PROJECTION_DISPATCHER_PROVIDER,
                    REST_RESOURCE, AUTO_DISPATCH_RESOURCE_HANDLER, EXCHANGE_BOOTSTRAP};

    return Stream.of(ContentQuery.findFullyQualifiedClassNames(context.contents(), dependencies),
            storageType.resolveTypeRegistryQualifiedNames(useCQRS)).flatMap(Set::stream)
            .collect(Collectors.toSet());
  }

  private String resolveExchangeBootstrapName(final CodeGenerationContext context) {
    final Optional<String> exchangeBootstrapQualifiedName =
            ContentQuery.findFullyQualifiedClassNames(EXCHANGE_BOOTSTRAP, context.contents()).stream().findFirst();

    if (!exchangeBootstrapQualifiedName.isPresent()) {
      return null;
    }

    return exchangeBootstrapQualifiedName.get();
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.XOOM_INITIALIZER;
  }

}