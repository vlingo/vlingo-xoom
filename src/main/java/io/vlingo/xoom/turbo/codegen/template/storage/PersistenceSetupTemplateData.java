// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.storage;

import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.QUERIES;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;

public class PersistenceSetupTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public static TemplateData from(final String basePackage,
                                    final String persistencePackage,
                                    final Boolean useCQRS,
                                    final StorageType storageType,
                                    final ProjectionType projectionType,
                                    final List<TemplateData> templatesData,
                                    final List<Content> contents) {
        return new PersistenceSetupTemplateData(basePackage, persistencePackage, useCQRS,
                storageType, projectionType, templatesData, contents);
    }

    private PersistenceSetupTemplateData(final String basePackage,
                                         final String persistencePackage,
                                         final Boolean useCQRS,
                                         final StorageType storageType,
                                         final ProjectionType projectionType,
                                         final List<TemplateData> templatesData,
                                         final List<Content> contents){
        this.templateParameters =
                loadParameters(basePackage, persistencePackage, useCQRS, storageType,
                        projectionType, templatesData, contents);
    }

    @SuppressWarnings("rawtypes")
    private TemplateParameters loadParameters(final String basePackage,
                                              final String persistencePackage,
                                              final Boolean useCQRS,
                                              final StorageType storageType,
                                              final ProjectionType projectionType,
                                              final List<TemplateData> templatesData,
                                              final List<Content> contents)  {
        return TemplateParameters.with(BASE_PACKAGE, basePackage)
                .and(PACKAGE_NAME, persistencePackage)
                .and(STORAGE_TYPE, storageType.name())
                .and(DATA_OBJECTS, resolveDataObjectNames(contents))
                .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(PROJECTION_TYPE, projectionType.name())
                .and(ADAPTERS, Adapter.from(templatesData))
                .and(PROJECTIONS, Projection.from(contents))
                .and(QUERIES, Queries.from(useCQRS, contents, templatesData))
                .and(USE_CQRS, useCQRS).and(USE_ANNOTATIONS, true)
                .addImports(resolveImports(useCQRS, contents))
                .andResolve(REQUIRE_ADAPTERS, params -> !params.<List>find(ADAPTERS).isEmpty());
    }

    @SuppressWarnings("unchecked")
    private Set<String> resolveImports(final boolean useCQRS, final List<Content> contents) {
        if(useCQRS) {
            return ContentQuery.findFullyQualifiedClassNames(contents, AGGREGATE_STATE, DATA_OBJECT, DOMAIN_EVENT);
        }
        return ContentQuery.findFullyQualifiedClassNames(contents, AGGREGATE_STATE, DATA_OBJECT);
    }

    private String resolveDataObjectNames(final List<Content> contents) {
        return ContentQuery.findClassNames(DATA_OBJECT, contents).stream()
                .map(name -> name + ".class").collect(Collectors.joining(", "));
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return PERSISTENCE_SETUP;
    }

}
