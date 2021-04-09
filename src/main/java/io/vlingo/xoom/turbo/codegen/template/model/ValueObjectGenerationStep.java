// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.model;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.turbo.codegen.template.model.valueobject.ValueObjectTemplateData;

import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.VALUE_OBJECT;

public class ValueObjectGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(CodeGenerationContext context) {
    final String basePackage = context.parameterOf(Label.PACKAGE);
    final Language language = context.parameterOf(Label.LANGUAGE, Language::valueOf);
    final Stream<CodeGenerationParameter> valueObjects = context.parametersOf(Label.VALUE_OBJECT);
    return ValueObjectTemplateData.from(basePackage, language, valueObjects);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.hasParameter(VALUE_OBJECT);
  }

}
