// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.aggregate;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.model.formatting.Formatters.Arguments.SIGNATURE_DECLARATION;
import static io.vlingo.xoom.codegen.template.model.formatting.Formatters.Fields.Style.*;

public class AggregateStateTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    @SuppressWarnings("unchecked")
    public AggregateStateTemplateData(final String packageName,
                                      final Language language,
                                      final CodeGenerationParameter aggregate,
                                      final StorageType storageType) {
        this.protocolName = aggregate.value;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName)
                        .and(EVENT_SOURCED, storageType.isSourced())
                        .and(MEMBERS, Formatters.Fields.format(MEMBER_DECLARATION, language, aggregate))
                        .and(MEMBERS_ASSIGNMENT, Formatters.Fields.format(ASSIGNMENT, language, aggregate))
                        .and(ID_TYPE, FieldDetail.typeOf(aggregate, "id"))
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(protocolName))
                        .and(CONSTRUCTOR_PARAMETERS, SIGNATURE_DECLARATION.format(aggregate))
                        .and(METHOD_INVOCATION_PARAMETERS, resolveIdBasedConstructorParameters(language, aggregate))
                        .and(METHODS, new ArrayList<String>());

        this.dependOn(AggregateStateMethodTemplateData.from(language, aggregate));
    }

    private String resolveIdBasedConstructorParameters(final Language language, final CodeGenerationParameter aggregate) {
        final CodeGenerationParameter idField = CodeGenerationParameter.of(STATE_FIELD, "id");
        return Formatters.Fields.format(ALTERNATE_REFERENCE_WITH_DEFAULT_VALUE, language, aggregate, Stream.of(idField));
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(METHODS).add(outcome);
    }

    @Override
    public String filename() {
        return standard().resolveFilename(protocolName, parameters);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return AGGREGATE_STATE;
    }

}