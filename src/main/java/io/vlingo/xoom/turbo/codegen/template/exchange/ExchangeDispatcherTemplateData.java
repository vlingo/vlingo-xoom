// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.EXCHANGE;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.ROLE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PRODUCER_EXCHANGES;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static java.util.stream.Collectors.toList;

public class ExchangeDispatcherTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static TemplateData from(final String exchangePackage,
                                    final Stream<CodeGenerationParameter> aggregates,
                                    final List<Content> contents) {
        final List<CodeGenerationParameter> producerExchanges =
                aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE))
                        .filter(aggregate -> aggregate.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer())
                        .collect(toList());

        return new ExchangeDispatcherTemplateData(exchangePackage, producerExchanges, contents);
    }

    private ExchangeDispatcherTemplateData(final String exchangePackage,
                                           final List<CodeGenerationParameter> producerExchanges,
                                           final List<Content> contents) {
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, exchangePackage)
                        .and(PRODUCER_EXCHANGES, ProducerExchange.from(producerExchanges))
                        .addImports(resolveImports(producerExchanges, contents));
    }

    private Set<String> resolveImports(final List<CodeGenerationParameter> producerExchanges,
                                       final List<Content> contents) {
        return producerExchanges.stream().flatMap(exchange -> exchange.retrieveAllRelated(Label.DOMAIN_EVENT))
                .map(event -> ContentQuery.findFullyQualifiedClassName(DOMAIN_EVENT, event.value, contents))
                .collect(Collectors.toSet());
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.EXCHANGE_DISPATCHER;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isPlaceholder() {
        return parameters.<List>find(PRODUCER_EXCHANGES).isEmpty();
    }
}
