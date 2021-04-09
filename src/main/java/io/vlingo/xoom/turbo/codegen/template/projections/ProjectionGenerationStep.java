// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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

import static io.vlingo.xoom.turbo.codegen.parameter.Label.PROJECTION_TYPE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.PROJECTION;

public class ProjectionGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        return ProjectionTemplateDataFactory.build(context);
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        if (context.isInternalGeneration()) {
            return ContentQuery.exists(PROJECTION, context.contents());
        } else if (ContentQuery.exists(AGGREGATE_PROTOCOL, context.contents())) {
            return context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf)
                    .isProjectionEnabled();
        }
        return false;
    }

}
