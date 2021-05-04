// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.projections;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateProcessingStep;

import java.util.List;

import static io.vlingo.xoom.turbo.codegen.designer.Label.PROJECTION_TYPE;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.AGGREGATE_PROTOCOL;

public class ProjectionGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    return ProjectionTemplateDataFactory.build(context);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
    return ContentQuery.exists(AGGREGATE_PROTOCOL, context.contents()) && projectionType.isProjectionEnabled();
  }

}
