// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.projections;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PROJECTION_SOURCES;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PROJECTION_SOURCE_TYPES_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PROJECTION_SOURCE_TYPES_QUALIFIED_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PROJECTION_TYPE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.PROJECTION_SOURCE_TYPES;

import java.util.List;

import io.vlingo.xoom.turbo.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

public class ProjectionSourceTypesTemplateData extends TemplateData {

    @SuppressWarnings("unused")
    private final static String PACKAGE_PATTERN = "%s.%s";


    private final TemplateParameters parameters;

    public static ProjectionSourceTypesTemplateData from(final String basePackage,
                                                         final ProjectionType projectionType,
                                                         final List<Content> contents) {
        return new ProjectionSourceTypesTemplateData(basePackage, projectionType, contents);
    }

    private ProjectionSourceTypesTemplateData(final String basePackage,
                                              final ProjectionType projectionType,
                                              final List<Content> contents) {
        final String packageName = resolvePackage(basePackage);

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName).and(PROJECTION_TYPE, projectionType)
                        .and(PROJECTION_SOURCES, ContentQuery.findClassNames(DOMAIN_EVENT, contents))
                        .and(PROJECTION_SOURCE_TYPES_NAME, resolveClassName(projectionType))
                        .andResolve(PROJECTION_SOURCE_TYPES_QUALIFIED_NAME, this::resolveQualifiedName);
    }

    private String resolvePackage(final String basePackage) {
        return ProjectionSourceTypesDetail.resolvePackage(basePackage);
    }

    private String resolveClassName(final ProjectionType projectionType) {
        return ProjectionSourceTypesDetail.resolveClassName(projectionType);
    }

    private String resolveQualifiedName(final TemplateParameters parameters) {
        final String packageName = parameters.find(PACKAGE_NAME);
        final String className = parameters.find(PROJECTION_SOURCE_TYPES_NAME);
        return CodeElementFormatter.qualifiedNameOf(packageName, className);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return PROJECTION_SOURCE_TYPES;
    }

    @Override
    public String filename() {
        return PROJECTION_SOURCE_TYPES.resolveFilename(parameters);
    }
}
