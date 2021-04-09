// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.dataobject;

import io.vlingo.xoom.turbo.TextExpectation;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.PACKAGE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;

public class DataObjectGenerationStepTest {

  @Test
  public void testThatDataObjectsAreGenerated() throws IOException {
    final CodeGenerationParameters parameters =
            CodeGenerationParameters.from(PACKAGE, "io.vlingo.xoomapp")
                    .add(Label.LANGUAGE, Language.JAVA)
                    .add(authorAggregate()).add(bookAggregate())
                    .add(nameValueObject()).add(rankValueObject())
                    .add(classificationValueObject()).add(classifierValueObject());

    final CodeGenerationContext context =
            CodeGenerationContext.with(parameters)
                    .contents(contents());

    new DataObjectGenerationStep().process(context);

    final Content authorData = context.findContent(TemplateStandard.DATA_OBJECT, "AuthorData");
    final Content nameDataValueObject = context.findContent(TemplateStandard.DATA_OBJECT, "NameData");
    final Content bookDataContent = context.findContent(TemplateStandard.DATA_OBJECT, "BookData");
    final Content rankDataContent = context.findContent(TemplateStandard.DATA_OBJECT, "RankData");
    final Content classificationDataContent = context.findContent(TemplateStandard.DATA_OBJECT, "ClassificationData");
    final Content classifierDataContent = context.findContent(TemplateStandard.DATA_OBJECT, "ClassifierData");

    Assert.assertEquals(14, context.contents().size());
    Assert.assertTrue(authorData.contains(TextExpectation.onJava().read("author-data")));
    Assert.assertTrue(nameDataValueObject.contains(TextExpectation.onJava().read("name-data")));
    Assert.assertTrue(bookDataContent.contains(TextExpectation.onJava().read("book-data")));
    Assert.assertTrue(rankDataContent.contains(TextExpectation.onJava().read("rank-data")));
    Assert.assertTrue(classificationDataContent.contains(TextExpectation.onJava().read("classification-data")));
    Assert.assertTrue(classifierDataContent.contains(TextExpectation.onJava().read("classifier-data")));
  }

  private CodeGenerationParameter authorAggregate() {
    final CodeGenerationParameter idField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                    .relate(Label.FIELD_TYPE, "String");

    final CodeGenerationParameter nameField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "name")
                    .relate(Label.FIELD_TYPE, "Name");

    final CodeGenerationParameter rankField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
                    .relate(Label.FIELD_TYPE, "Rank");

    final CodeGenerationParameter statusField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "status")
                    .relate(Label.FIELD_TYPE, "boolean");

    return CodeGenerationParameter.of(Label.AGGREGATE, "Author")
            .relate(idField).relate(nameField).relate(rankField).relate(statusField);
  }

  private CodeGenerationParameter bookAggregate() {
    final CodeGenerationParameter idField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                    .relate(Label.FIELD_TYPE, "String");

    final CodeGenerationParameter nameField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "title")
                    .relate(Label.FIELD_TYPE, "String");

    final CodeGenerationParameter rankField =
            CodeGenerationParameter.of(Label.STATE_FIELD, "publisher")
                    .relate(Label.FIELD_TYPE, "String");

    return CodeGenerationParameter.of(Label.AGGREGATE, "Book")
            .relate(idField).relate(nameField).relate(rankField);
  }

  private CodeGenerationParameter nameValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Name")
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "firstName")
                    .relate(Label.FIELD_TYPE, "String"))
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "lastName")
                    .relate(Label.FIELD_TYPE, "String"));
  }

  private CodeGenerationParameter rankValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Rank")
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "points")
                    .relate(Label.FIELD_TYPE, "int"))
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classification")
                    .relate(Label.FIELD_TYPE, "Classification"));
  }

  private CodeGenerationParameter classificationValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classification")
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "label")
                    .relate(Label.FIELD_TYPE, "String"))
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classifier")
                    .relate(Label.FIELD_TYPE, "Classifier"));
  }

  private CodeGenerationParameter classifierValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classifier")
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "name")
                    .relate(Label.FIELD_TYPE, "String"));

  }

  private Content[] contents() {
    return new Content[]{
            Content.with(AGGREGATE_STATE, new OutputFile("/Projects/", "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
            Content.with(AGGREGATE_STATE, new OutputFile("/Projects/", "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
            Content.with(VALUE_OBJECT, new OutputFile("/Projects/", "Rank.java"), null, null, RANK_VALUE_OBJECT_CONTENT_TEXT),
            Content.with(VALUE_OBJECT, new OutputFile("/Projects/", "Name.java"), null, null, NAME_VALUE_OBJECT_CONTENT_TEXT),
            Content.with(VALUE_OBJECT, new OutputFile("/Projects/", "Classification.java"), null, null, CLASSIFICATION_VALUE_OBJECT_CONTENT_TEXT),
            Content.with(VALUE_OBJECT, new OutputFile("/Projects/", "Classifier.java"), null, null, CLASSIFIER_VALUE_OBJECT_CONTENT_TEXT),
            Content.with(AGGREGATE_PROTOCOL, new OutputFile("/Projects/", "Author.java"), null, null, AUTHOR_PROTOCOL_CONTENT_TEXT),
            Content.with(AGGREGATE_PROTOCOL, new OutputFile("/Projects/", "Book.java"), null, null, BOOK_PROTOCOL_CONTENT_TEXT)
    };
  }

  private static final String AUTHOR_STATE_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public class AuthorState { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_STATE_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public class BookState { \\n" +
                  "... \\n" +
                  "}";

  private static final String AUTHOR_PROTOCOL_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public class Author { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_PROTOCOL_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public class Book { \\n" +
                  "... \\n" +
                  "}";

  private static final String NAME_VALUE_OBJECT_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model; \\n" +
                  "public class Name { \\n" +
                  "... \\n" +
                  "}";

  private static final String RANK_VALUE_OBJECT_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model; \\n" +
                  "public class Rank { \\n" +
                  "... \\n" +
                  "}";

  private static final String CLASSIFICATION_VALUE_OBJECT_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model; \\n" +
                  "public class Classification { \\n" +
                  "... \\n" +
                  "}";

  private static final String CLASSIFIER_VALUE_OBJECT_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model; \\n" +
                  "public class Classifier { \\n" +
                  "... \\n" +
                  "}";


}
