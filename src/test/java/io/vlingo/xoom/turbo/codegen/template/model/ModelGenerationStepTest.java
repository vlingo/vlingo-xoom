package io.vlingo.xoom.turbo.codegen.template.model;

import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.TextExpectation;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.designer.Label;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.turbo.codegen.designer.Label.FACTORY_METHOD;
import static io.vlingo.xoom.turbo.codegen.designer.Label.PACKAGE;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.*;
import static io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType.*;
import static io.vlingo.xoom.turbo.codegen.template.storage.StorageType.JOURNAL;
import static io.vlingo.xoom.turbo.codegen.template.storage.StorageType.STATE_STORE;

public class ModelGenerationStepTest {

    @Test
    public void testThatEventBasedStatefulModelIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, STATE_STORE),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, EVENT_BASED),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.JAVA),
                        CodeGenerationParameter.of(Label.CQRS, true),
                        authorAggregate(), nameValueObject(), rankValueObject());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content authorProtocol = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(authorProtocol.contains(TextExpectation.onJava().read("author-protocol")));
        Assert.assertTrue(authorEntity.contains(TextExpectation.onJava().read("event-based-stateful-author-entity")));
        Assert.assertTrue(authorState.contains(TextExpectation.onJava().read("stateful-author-state")));
        Assert.assertTrue(authorRegistered.contains(TextExpectation.onJava().read("author-registered")));
        Assert.assertTrue(authorRanked.contains(TextExpectation.onJava().read("author-ranked")));
    }

    @Test
    public void testThatOperationBasedStatefulModelIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, STATE_STORE),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, OPERATION_BASED),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.JAVA),
                        CodeGenerationParameter.of(Label.CQRS, true),
                        authorAggregate(), nameValueObject(), rankValueObject());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content authorProtocol = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(authorProtocol.contains(TextExpectation.onJava().read("author-protocol")));
        Assert.assertTrue(authorEntity.contains(TextExpectation.onJava().read("operation-based-stateful-author-entity")));
        Assert.assertTrue(authorState.contains(TextExpectation.onJava().read("stateful-author-state")));
        Assert.assertTrue(authorRegistered.contains(TextExpectation.onJava().read("author-registered")));
        Assert.assertTrue(authorRanked.contains(TextExpectation.onJava().read("author-ranked")));
    }

    @Test
    public void testThatOperationBasedStatefulSingleModelIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, STATE_STORE),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, NONE),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.JAVA),
                        CodeGenerationParameter.of(Label.CQRS, false),
                        authorAggregate(), nameValueObject(), rankValueObject());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content authorProtocol = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(authorProtocol.contains(TextExpectation.onJava().read("author-protocol-for-single-model")));
        Assert.assertTrue(authorEntity.contains(TextExpectation.onJava().read("operation-based-stateful-single-model-author-entity")));
        Assert.assertTrue(authorState.contains(TextExpectation.onJava().read("stateful-author-state")));
        Assert.assertTrue(authorRegistered.contains(TextExpectation.onJava().read("author-registered")));
        Assert.assertTrue(authorRanked.contains(TextExpectation.onJava().read("author-ranked")));
    }

    @Test
    public void testThatSourcedModelIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, JOURNAL),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, EVENT_BASED),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.JAVA),
                        CodeGenerationParameter.of(Label.CQRS, true),
                        authorAggregate(), nameValueObject(), rankValueObject());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content authorProtocol = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(authorProtocol.contains(TextExpectation.onJava().read("author-protocol")));
        Assert.assertTrue(authorEntity.contains(TextExpectation.onJava().read("sourced-author-entity")));
        Assert.assertTrue(authorState.contains(TextExpectation.onJava().read("sourced-author-state")));
        Assert.assertTrue(authorRegistered.contains(TextExpectation.onJava().read("author-registered")));
        Assert.assertTrue(authorRanked.contains(TextExpectation.onJava().read("author-ranked")));
    }

    @Test
    public void testThatSourcedSingleModelIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, JOURNAL),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, EVENT_BASED),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.JAVA),
                        CodeGenerationParameter.of(Label.CQRS, false),
                        authorAggregate(), nameValueObject(), rankValueObject());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content authorProtocol = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(authorProtocol.contains(TextExpectation.onJava().read("author-protocol-for-single-model")));
        Assert.assertTrue(authorEntity.contains(TextExpectation.onJava().read("sourced-single-model-author-entity")));
        Assert.assertTrue(authorState.contains(TextExpectation.onJava().read("sourced-author-state")));
        Assert.assertTrue(authorRegistered.contains(TextExpectation.onJava().read("author-registered")));
        Assert.assertTrue(authorRanked.contains(TextExpectation.onJava().read("author-ranked")));
    }

    @Test
    @Ignore
    public void testThatStatefulModelIsGeneratedOnKotlin() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, STATE_STORE),
                        CodeGenerationParameter.of(Label.PROJECTION_TYPE, EVENT_BASED),
                        CodeGenerationParameter.of(Label.LANGUAGE, Language.KOTLIN),
                        authorAggregate());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        final Content author = context.findContent(AGGREGATE_PROTOCOL, "Author");
        final Content authorEntity = context.findContent(AGGREGATE, "AuthorEntity");
        final Content authorState = context.findContent(AGGREGATE_STATE, "AuthorState");
        final Content authorRegistered = context.findContent(DOMAIN_EVENT, "AuthorRegistered");
        final Content authorRanked = context.findContent(DOMAIN_EVENT, "AuthorRanked");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(author.contains("interface Author "));
        Assert.assertTrue(author.contains("val _address = stage.addressFactory().uniquePrefixedWith(\"g-\") : Address"));
        Assert.assertTrue(author.contains("val _author = stage.actorFor(Author::class.java, Definition.has(AuthorEntity::class.java, Definition.parameters(_address.idString())), _address) : Author"));
        Assert.assertTrue(author.contains("return _author.withName(name)"));
        Assert.assertTrue(authorEntity.contains("public class AuthorEntity : StatefulEntity<AuthorState>, Author"));
        Assert.assertTrue(authorEntity.contains("public fun withName(final Name name): Completes<AuthorState>"));
        Assert.assertTrue(authorEntity.contains("val stateArg: AuthorState = state.withName(name)"));
        Assert.assertTrue(authorEntity.contains("return apply(stateArg, AuthorRegistered(stateArg)){state}"));
        Assert.assertTrue(authorState.contains("class AuthorState"));
        Assert.assertTrue(authorState.contains("val id: String;"));
        Assert.assertTrue(authorState.contains("val name: Name;"));
        Assert.assertTrue(authorState.contains("val rank: Rank;"));
        Assert.assertTrue(authorRegistered.contains("class AuthorRegistered : IdentifiedDomainEvent"));
        Assert.assertTrue(authorRegistered.contains("val id: String;"));
        Assert.assertTrue(authorRegistered.contains("val name: Name;"));
        Assert.assertTrue(authorRanked.contains("class AuthorRanked : IdentifiedDomainEvent"));
        Assert.assertTrue(authorRanked.contains("val id: String;"));
        Assert.assertTrue(authorRanked.contains("val rank: Rank;"));
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

        final CodeGenerationParameter authorRegisteredEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRegistered")
                        .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
                        .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "name"));

        final CodeGenerationParameter authorRankedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRanked")
                        .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
                        .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "rank"));

        final CodeGenerationParameter factoryMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "withName")
                        .relate(Label.METHOD_PARAMETER, "name")
                        .relate(FACTORY_METHOD, "true")
                        .relate(authorRegisteredEvent);

        final CodeGenerationParameter rankMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "changeRank")
                        .relate(Label.METHOD_PARAMETER, "rank")
                        .relate(authorRankedEvent);

        final CodeGenerationParameter hideMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "hide");

        return CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                .relate(idField).relate(nameField).relate(rankField)
                .relate(factoryMethod).relate(rankMethod).relate(hideMethod)
                .relate(authorRegisteredEvent).relate(authorRankedEvent);
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
                                .relate(Label.FIELD_TYPE, "String"));
    }

    private Content[] contents() {
        return new Content[] {
                Content.with(VALUE_OBJECT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH).toString(), "Rank.java"), null, null, RANK_VALUE_OBJECT_CONTENT_TEXT),
                Content.with(VALUE_OBJECT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Name.java"), null, null, NAME_VALUE_OBJECT_CONTENT_TEXT)
        };
    }

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

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

}
