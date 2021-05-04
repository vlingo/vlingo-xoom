// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.turbo.annotation.codegen.template.Label;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;

import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.designer.Label.FACTORY_METHOD;
import static io.vlingo.xoom.turbo.codegen.designer.Label.LANGUAGE;

public class CodeGenerationParametersBuilder {

    public static Stream<CodeGenerationParameter> threeExchanges() {
        final CodeGenerationParameter language =
                CodeGenerationParameter.of(LANGUAGE, Language.JAVA);

        final CodeGenerationParameter idField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                        .relate(Label.FIELD_TYPE, "String");

        final CodeGenerationParameter nameField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "name")
                        .relate(Label.FIELD_TYPE, "Name");

        final CodeGenerationParameter rankField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
                        .relate(Label.FIELD_TYPE, "Rank");

        final CodeGenerationParameter factoryMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "withName")
                        .relate(Label.METHOD_PARAMETER, "name")
                        .relate(FACTORY_METHOD, "true");

        final CodeGenerationParameter rankMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "changeRank")
                        .relate(Label.METHOD_PARAMETER, "rank");

        final CodeGenerationParameter blockMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "block")
                        .relate(Label.METHOD_PARAMETER, "name");

        final CodeGenerationParameter otherAppExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "otherapp-exchange")
                        .relate(Label.ROLE, ExchangeRole.CONSUMER)
                        .relate(CodeGenerationParameter.of(Label.RECEIVER)
                                .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.xoom.otherapp:OtherAggregateDefined:0.0.1")
                                .relate(Label.MODEL_METHOD, "withName"))
                        .relate(CodeGenerationParameter.of(Label.RECEIVER)
                                .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.xoom.otherapp:OtherAggregateUpdated:0.0.2")
                                .relate(Label.MODEL_METHOD, "changeRank"))
                        .relate(CodeGenerationParameter.of(Label.RECEIVER)
                                .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.xoom.otherapp:OtherAggregateRemoved:0.0.3")
                                .relate(Label.MODEL_METHOD, "block"));

        final CodeGenerationParameter authorExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "author-exchange")
                        .relate(Label.ROLE, ExchangeRole.PRODUCER)
                        .relate(Label.SCHEMA_GROUP, "vlingo:xoom:io.vlingo.xoomapp")
                        .relate(Label.DOMAIN_EVENT, "AuthorBlocked")
                        .relate(Label.DOMAIN_EVENT, "AuthorRated");

        final CodeGenerationParameter authorRatedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRated")
                        .relate(idField).relate(rankField);

        final CodeGenerationParameter authorBlockedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorBlocked")
                        .relate(nameField);

        final CodeGenerationParameter authorAggregate =
                CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                        .relate(otherAppExchange).relate(authorExchange)
                        .relate(idField).relate(nameField).relate(rankField)
                        .relate(factoryMethod).relate(rankMethod).relate(blockMethod)
                        .relate(authorRatedEvent).relate(authorBlockedEvent);

        final CodeGenerationParameter titleField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "name")
                        .relate(Label.FIELD_TYPE, "Name");

        final CodeGenerationParameter priceField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
                        .relate(Label.FIELD_TYPE, "int");

        final CodeGenerationParameter bookExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "book-exchange")
                        .relate(Label.ROLE, ExchangeRole.PRODUCER)
                        .relate(Label.SCHEMA_GROUP, "vlingo:xoom:io.vlingo.xoomapp")
                        .relate(Label.DOMAIN_EVENT, "BookSoldOut")
                        .relate(Label.DOMAIN_EVENT, "BookPurchased");

        final CodeGenerationParameter bookSoldOutEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "BookSoldOut")
                        .relate(titleField);

        final CodeGenerationParameter bookPurchasedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "BookPurchased")
                        .relate(priceField);

        final CodeGenerationParameter bookAggregate =
                CodeGenerationParameter.of(Label.AGGREGATE, "Book")
                        .relate(titleField).relate(priceField)
                        .relate(bookSoldOutEvent).relate(bookPurchasedEvent)
                        .relate(bookExchange);

        final CodeGenerationParameter nameValueObject =
             CodeGenerationParameter.of(Label.VALUE_OBJECT, "Name")
                    .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "firstName")
                            .relate(Label.FIELD_TYPE, "String"))
                    .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "lastName")
                            .relate(Label.FIELD_TYPE, "String"));

        final CodeGenerationParameter rankValueObject =
            CodeGenerationParameter.of(Label.VALUE_OBJECT, "Rank")
                    .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "points")
                            .relate(Label.FIELD_TYPE, "int"))
                    .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classification")
                            .relate(Label.FIELD_TYPE, "Classification"));

        final CodeGenerationParameter classificationValueObject =
                CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classification")
                        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "label")
                                .relate(Label.FIELD_TYPE, "String"))
                        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classifier")
                                .relate(Label.FIELD_TYPE, "Classifier"));

        final CodeGenerationParameter classifierValueObject =
                CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classifier")
                        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "name")
                                .relate(Label.FIELD_TYPE, "String"));

        return Stream.of(language, authorAggregate, bookAggregate, nameValueObject,
                rankValueObject, classificationValueObject, classifierValueObject);
    }


}
