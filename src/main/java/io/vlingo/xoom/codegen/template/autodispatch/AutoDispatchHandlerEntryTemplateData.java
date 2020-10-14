// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.MethodScope;

import java.beans.Introspector;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.FACTORY_METHOD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.ENTITY_DATA;
import static io.vlingo.xoom.codegen.template.model.AggregateArgumentsFormat.DATA_BASED_METHOD_INVOCATION;

public class AutoDispatchHandlerEntryTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAll(Label.ROUTE_SIGNATURE)
                .filter(route -> !route.hasAny(READ_ONLY))
                .map(AutoDispatchHandlerEntryTemplateData::new)
                .collect(Collectors.toList());
    }

    private AutoDispatchHandlerEntryTemplateData(final CodeGenerationParameter route) {
        final CodeGenerationParameter aggregate = route.parent(AGGREGATE);
        final CodeGenerationParameter method = findMethod(aggregate, route);
        final boolean factoryMethod = method.relatedParameterValueOf(Label.FACTORY_METHOD, Boolean::valueOf);
        final MethodScope methodScope = factoryMethod ? MethodScope.STATIC : MethodScope.INSTANCE;

        this.parameters =
                TemplateParameters.with(METHOD_NAME, route.value)
                        .and(FACTORY_METHOD, factoryMethod)
                        .and(AGGREGATE_PROTOCOL_NAME, aggregate.value)
                        .and(ENTITY_DATA_NAME, ENTITY_DATA.resolveClassname(aggregate.value))
                        .and(AGGREGATE_PROTOCOL_VARIABLE, Introspector.decapitalize(aggregate.value))
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(aggregate.value))
                        .and(INDEX_NAME, AutoDispatchMappingValueFormatter.format(route.value))
                        .and(METHOD_INVOCATION_PARAMETERS, DATA_BASED_METHOD_INVOCATION.format(method, methodScope));
    }

    private CodeGenerationParameter findMethod(final CodeGenerationParameter aggregate,
                                               final CodeGenerationParameter route) {
        return aggregate.retrieveAll(AGGREGATE_METHOD)
                .filter(method -> method.value.equals(route.value))
                .findFirst().get();
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
