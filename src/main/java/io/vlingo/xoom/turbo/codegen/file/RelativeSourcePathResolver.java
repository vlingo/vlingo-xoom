// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.file;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;

public interface RelativeSourcePathResolver {

  static String[] resolveWith(final CodeGenerationContext context, final TemplateData templateData) {
    final RelativeSourcePathResolver resolver =
            Stream.of(new ResourceFile(), new SchemataSpecification(), new PomFile(),
                    new ProjectGenerationSettingsPayload(), new SourceCode())
                    .filter(candidate -> candidate.shouldResolve(templateData))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unable to resolve relative source path"));

    return resolver.resolve(context, templateData);
  }

  String[] resolve(final CodeGenerationContext context, final TemplateData templateData);

  boolean shouldResolve(final TemplateData templateData);

  class ResourceFile implements RelativeSourcePathResolver {

    @Override
    public String[] resolve(final CodeGenerationContext context, final TemplateData templateData) {
      return new String[]{"src", "main", "resources"};
    }

    @Override
    public boolean shouldResolve(final TemplateData templateData) {
      return templateData.parameters().find(RESOURCE_FILE, false);
    }
  }

  class SchemataSpecification implements RelativeSourcePathResolver {

    @Override
    public String[] resolve(final CodeGenerationContext context, final TemplateData templateData) {
      return new String[]{"src", "main", "vlingo","schemata"};
    }

    @Override
    public boolean shouldResolve(final TemplateData templateData) {
      return templateData.parameters().find(SCHEMATA_FILE, false);
    }
  }

  class PomFile implements RelativeSourcePathResolver {

    @Override
    public String[] resolve(final CodeGenerationContext context, final TemplateData templateData) {
      return new String[]{};
    }

    @Override
    public boolean shouldResolve(final TemplateData templateData) {
      return templateData.parameters().find(POM_SECTION, false);
    }
  }

  class ProjectGenerationSettingsPayload implements RelativeSourcePathResolver {

    @Override
    public String[] resolve(final CodeGenerationContext context, final TemplateData templateData) {
      return new String[]{};
    }

    @Override
    public boolean shouldResolve(final TemplateData templateData) {
      return templateData.parameters().find(PROJECT_SETTINGS, false);
    }
  }

  class SourceCode implements RelativeSourcePathResolver {

    @Override
    public String[] resolve(final CodeGenerationContext context, final TemplateData templateData) {
      final Language language = context.language();
      final String packageName = templateData.parameters().find(PACKAGE_NAME);
      return ArrayUtils.addAll(language.sourceFolder, packageName.split("\\."));
    }

    @Override
    public boolean shouldResolve(final TemplateData templateData) {
      return templateData.parameters().find(SOURCE_CODE, false);
    }
  }

}
