// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.storage;

import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageTemplateDataFactory {

  private final static String PACKAGE_PATTERN = "%s.%s.%s";
  private final static String PARENT_PACKAGE_NAME = "infrastructure";
  private final static String PERSISTENCE_PACKAGE_NAME = "persistence";

  public static List<TemplateData> build(final String basePackage,
                                         final String appName,
                                         final List<Content> contents,
                                         final StorageType storageType,
                                         final Map<Model, DatabaseType> databases,
                                         final ProjectionType projectionType,
                                         final Boolean internalGeneration,
                                         final Boolean useAnnotations,
                                         final Boolean useCQRS) {
    final String persistencePackage = resolvePackage(basePackage);

    final List<TemplateData> templatesData = new ArrayList<>();

    templatesData.addAll(AdapterTemplateData.from(persistencePackage, storageType, contents));

    if(!internalGeneration) {
      templatesData.addAll(QueriesTemplateDataFactory.from(persistencePackage, useCQRS, contents));
      templatesData.add(new DatabasePropertiesTemplateData(appName, databases));
    }

    templatesData.addAll(buildStoreProvidersTemplateData(persistencePackage,
            useCQRS, useAnnotations, storageType, projectionType, templatesData,
            contents));

    if(useAnnotations) {
      templatesData.add(PersistenceSetupTemplateData.from(basePackage, persistencePackage,
              useCQRS, storageType, projectionType, templatesData, contents));
    }

    return templatesData;
  }

  private static List<TemplateData> buildStoreProvidersTemplateData(final String persistencePackage,
                                                                    final Boolean useCQRS,
                                                                    final Boolean useAnnotations,
                                                                    final StorageType storageType,
                                                                    final ProjectionType projectionType,
                                                                    final List<TemplateData> templatesData,
                                                                    final List<Content> contents) {
    return StorageProviderTemplateData.from(persistencePackage, storageType, projectionType,
            templatesData, contents, Model.applicableTo(useCQRS), useAnnotations);
  }

  private static String resolvePackage(final String basePackage) {
    if(basePackage.endsWith(".infrastructure")) {
      return basePackage + "." + PERSISTENCE_PACKAGE_NAME;
    }
    return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME).toLowerCase();
  }

}
