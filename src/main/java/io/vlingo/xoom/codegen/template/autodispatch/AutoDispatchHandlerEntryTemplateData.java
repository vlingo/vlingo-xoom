// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.formatting.AggregateMethodInvocation;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;
import static io.vlingo.xoom.codegen.parameter.Label.READ_ONLY;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;
import static io.vlingo.xoom.codegen.template.model.formatting.Formatters.Variables.Style.VALUE_OBJECT_INITIALIZER;

public class AutoDispatchHandlerEntryTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final Language language,
                                          final CodeGenerationParameter aggregate,
                                          final List<CodeGenerationParameter> valueObjects) {
        return aggregate.retrieveAllRelated(Label.ROUTE_SIGNATURE).filter(route -> !route.hasAny(READ_ONLY))
                .map(route -> new AutoDispatchHandlerEntryTemplateData(language, route, valueObjects))
                .collect(Collectors.toList());
    }

    private AutoDispatchHandlerEntryTemplateData(final Language language,
                                                 final CodeGenerationParameter route,
                                                 final List<CodeGenerationParameter> valueObjects) {
        final CodeGenerationParameter aggregate = route.parent(AGGREGATE);
        final CodeGenerationParameter method = AggregateDetail.methodWithName(aggregate, route.value);
        final boolean factoryMethod = method.retrieveRelatedValue(Label.FACTORY_METHOD, Boolean::valueOf);
        final List<String> valueObjectInitializers =
                Formatters.Variables.format(VALUE_OBJECT_INITIALIZER, language, method, valueObjects.stream());

        this.parameters =
                TemplateParameters.with(METHOD_NAME, route.value)
                        .and(FACTORY_METHOD, factoryMethod)
                        .and(AGGREGATE_PROTOCOL_NAME, aggregate.value)
                        .and(STATE_DATA_OBJECT_NAME, DATA_OBJECT.resolveClassname(aggregate.value))
                        .and(AGGREGATE_PROTOCOL_VARIABLE, ClassFormatter.simpleNameToAttribute(aggregate.value))
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(aggregate.value))
                        .and(INDEX_NAME, AutoDispatchMappingValueFormatter.format(route.value))
                        .and(METHOD_INVOCATION_PARAMETERS, resolveMethodInvocationParameters(method))
                        .and(VALUE_OBJECT_INITIALIZERS, valueObjectInitializers);
    }

    private String resolveMethodInvocationParameters(final CodeGenerationParameter method) {
        final boolean factoryMethod = method.retrieveRelatedValue(Label.FACTORY_METHOD, Boolean::valueOf);
        final MethodScope methodScope = factoryMethod ? MethodScope.STATIC : MethodScope.INSTANCE;
        return new AggregateMethodInvocation("$stage", "data").format(method, methodScope);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AUTO_DISPATCH_HANDLER_ENTRY;
    }
}
