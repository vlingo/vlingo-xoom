// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.projections;

import io.vlingo.xoom.turbo.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PROJECTION_TO_DESCRIPTION;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;

public class ProjectionDispatcherProviderTemplateData extends TemplateData {

  private static final String PACKAGE_PATTERN = "%s.%s.%s";
  private static final String PARENT_PACKAGE_NAME = "infrastructure";
  private static final String PERSISTENCE_PACKAGE_NAME = "persistence";

  private final TemplateParameters templateParameters;
  private final boolean placeholder;

  public static ProjectionDispatcherProviderTemplateData from(final String basePackage,
                                                              final ProjectionType projectionType,
                                                              final Boolean useAnnotations,
                                                              final List<Content> contents) {
    return new ProjectionDispatcherProviderTemplateData(basePackage, projectionType, useAnnotations, contents);
  }

  public static ProjectionDispatcherProviderTemplateData fromProjectionAnnotation(final ProjectionType projectionType,
                                                                                  final Stream<CodeGenerationParameter> projectionActors,
                                                                                  final List<Content> contents) {
    return new ProjectionDispatcherProviderTemplateData(projectionType, projectionActors, contents);
  }

  private ProjectionDispatcherProviderTemplateData(final ProjectionType projectionType,
                                                   final Stream<CodeGenerationParameter> projectionActors,
                                                   final List<Content> contents) {
    final String packageName = ContentQuery.findPackage(PROJECTION, contents);

    final List<ProjectToDescription> projectToDescriptionEntries =
            ProjectToDescription.fromProjectionAnnotation(projectionType, projectionActors.collect(Collectors.toList()));

    this.templateParameters = TemplateParameters.with(PACKAGE_NAME, packageName)
            .and(PROJECTION_TO_DESCRIPTION, projectToDescriptionEntries);

    this.placeholder = false;
  }

  private ProjectionDispatcherProviderTemplateData(final String basePackage,
                                                   final ProjectionType projectionType,
                                                   final boolean placeholder,
                                                   final List<Content> contents) {
    final String packageName = resolvePackage(basePackage);

    final List<ProjectToDescription> projectToDescriptionEntries =
            ProjectToDescription.from(projectionType, contents);

    this.templateParameters = TemplateParameters.with(PACKAGE_NAME, packageName)
            .and(PROJECTION_TO_DESCRIPTION, projectToDescriptionEntries)
            .addImports(resolveImports(basePackage, projectionType, contents));

    this.placeholder = placeholder;
  }

  private String resolvePackage(final String basePackage) {
    return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME).toLowerCase();
  }

  private Set<String> resolveImports(final String basePackage,
                                     final ProjectionType projectionType,
                                     final List<Content> contents) {
    if (projectionType.isOperationBased()) {
      final String projectionSourceTypesQualifiedName =
              ProjectionSourceTypesDetail.resolveQualifiedName(basePackage, projectionType);

      final String allSourceTypes =
              CodeElementFormatter.staticallyImportAllFrom(projectionSourceTypesQualifiedName);

      return Stream.of(allSourceTypes).collect(Collectors.toSet());
    }

    return ContentQuery.findFullyQualifiedClassNames(DOMAIN_EVENT, contents);
  }

  @Override
  public TemplateParameters parameters() {
    return templateParameters;
  }

  @Override
  public TemplateStandard standard() {
    return PROJECTION_DISPATCHER_PROVIDER;
  }

  @Override
  public boolean isPlaceholder() {
    return placeholder;
  }

}
