// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.storage;

import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.TextExpectation;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType.NONE;
import static io.vlingo.xoom.turbo.codegen.template.storage.DatabaseType.*;
import static io.vlingo.xoom.turbo.codegen.template.storage.StorageType.JOURNAL;
import static io.vlingo.xoom.turbo.codegen.template.storage.StorageType.STATE_STORE;

public class StorageGenerationStepTest {

  @Test
  public void testJournalGenerationWithHSQLDBDatabase() throws IOException {
    final CodeGenerationContext context =
            CodeGenerationContext.empty();

    loadProperties(context, JOURNAL, IN_MEMORY, NONE, true);
    loadContents(context);

    new StorageGenerationStep().process(context);

    final Content bookRentedAdapter = context.findContent(ADAPTER, "BookRentedAdapter");
    final Content bookPurchasedAdapter = context.findContent(ADAPTER, "BookPurchasedAdapter");
    final Content commandModelJournalProvider = context.findContent(STORE_PROVIDER, "CommandModelJournalProvider");
    final Content databaseProperties = context.findContent(DATABASE_PROPERTIES, "xoom-turbo");

    Assert.assertEquals(20, context.contents().size());
    Assert.assertTrue(bookRentedAdapter.contains(TextExpectation.onJava().read("book-rented-entry-adapter")));
    Assert.assertTrue(bookPurchasedAdapter.contains(TextExpectation.onJava().read("book-purchased-entry-adapter")));
    Assert.assertTrue(commandModelJournalProvider.contains(TextExpectation.onJava().read("command-model-journal-provider")));
    Assert.assertTrue(databaseProperties.contains(TextExpectation.onJava().read("in-memory-database-properties")));
  }

  @Test
  public void testStateStoreGenerationWithoutProjections() throws IOException {
    final CodeGenerationContext context =
            CodeGenerationContext.empty();

    loadProperties(context, STATE_STORE, HSQLDB, NONE, false);
    loadContents(context);

    new StorageGenerationStep().process(context);

    final Content bookStateAdapter = context.findContent(ADAPTER, "BookStateAdapter");
    final Content authorStateAdapter = context.findContent(ADAPTER, "AuthorStateAdapter");
    final Content stateStoreProvider = context.findContent(STORE_PROVIDER, "StateStoreProvider");
    final Content databaseProperties = context.findContent(DATABASE_PROPERTIES, "xoom-turbo");

    Assert.assertEquals(15, context.contents().size());
    Assert.assertTrue(bookStateAdapter.contains(TextExpectation.onJava().read("book-state-adapter")));
    Assert.assertTrue(authorStateAdapter.contains(TextExpectation.onJava().read("author-state-adapter")));
    Assert.assertTrue(stateStoreProvider.contains(TextExpectation.onJava().read("state-store-provider")));
    Assert.assertTrue(databaseProperties.contains(TextExpectation.onJava().read("hsqldb-database-properties")));
  }

  @Test
  public void testStateStoreGenerationWithProjections() throws IOException {
    final CodeGenerationContext context =
            CodeGenerationContext.empty();

    loadProperties(context, STATE_STORE, POSTGRES, EVENT_BASED, true);
    loadContents(context);

    new StorageGenerationStep().process(context);

    final Content bookStateAdapter = context.findContent(ADAPTER, "BookStateAdapter");
    final Content authorStateAdapter = context.findContent(ADAPTER, "AuthorStateAdapter");
    final Content commandModelStateStoreProvider = context.findContent(STORE_PROVIDER, "CommandModelStateStoreProvider");
    final Content queryModelStateStoreProvider = context.findContent(STORE_PROVIDER, "QueryModelStateStoreProvider");
    final Content databaseProperties = context.findContent(DATABASE_PROPERTIES, "xoom-turbo");

    Assert.assertEquals(20, context.contents().size());
    Assert.assertTrue(bookStateAdapter.contains(TextExpectation.onJava().read("book-state-adapter")));
    Assert.assertTrue(authorStateAdapter.contains(TextExpectation.onJava().read("author-state-adapter")));
    Assert.assertTrue(commandModelStateStoreProvider.contains(TextExpectation.onJava().read("command-model-state-store-provider")));
    Assert.assertTrue(queryModelStateStoreProvider.contains(TextExpectation.onJava().read("query-model-state-store-provider")));
    Assert.assertTrue(databaseProperties.contains(TextExpectation.onJava().read("postgres-database-properties")));
  }

  @Test
  public void testAnnotatedStoreGeneration() {
    final CodeGenerationContext context =
            CodeGenerationContext.empty().with(USE_ANNOTATIONS, "true");

    loadProperties(context, STATE_STORE, IN_MEMORY, EVENT_BASED, true);
    loadContents(context);

    new StorageGenerationStep().process(context);

    Assert.assertEquals(18, context.contents().size());
    Assert.assertEquals("PersistenceSetup", context.contents().get(17).retrieveName());
    Assert.assertTrue(context.contents().get(17).contains("class PersistenceSetup"));
    Assert.assertTrue(context.contents().get(17).contains("@Persistence(basePackage = \"io.vlingo\", storageType = StorageType.STATE_STORE, cqrs = true)"));
    Assert.assertTrue(context.contents().get(17).contains("@Projections(value = {"));
    Assert.assertTrue(context.contents().get(17).contains("@Projection(actor = AuthorProjectionActor.class, becauseOf = {}),"));
    Assert.assertTrue(context.contents().get(17).contains("@Projection(actor = BookProjectionActor.class, becauseOf = {BookRented.class, BookPurchased.class})"));
    Assert.assertTrue(context.contents().get(17).contains("@Adapters({"));
    Assert.assertTrue(context.contents().get(17).contains("BookState.class,"));
    Assert.assertTrue(context.contents().get(17).contains("AuthorState.class"));
    Assert.assertFalse(context.contents().get(17).contains("AuthorState.class,"));
    Assert.assertTrue(context.contents().get(17).contains("import io.vlingo.xoom.turbo.annotation.persistence.EnableQueries;"));
    Assert.assertTrue(context.contents().get(17).contains("import io.vlingo.xoom.turbo.annotation.persistence.QueriesEntry;"));
    Assert.assertTrue(context.contents().get(17).contains("import io.vlingo.xoom.turbo.annotation.persistence.DataObjects;"));
    Assert.assertTrue(context.contents().get(17).contains("@EnableQueries({"));
    Assert.assertTrue(context.contents().get(17).contains("@QueriesEntry(protocol = AuthorQueries.class, actor = AuthorQueriesActor.class)"));
    Assert.assertTrue(context.contents().get(17).contains("@QueriesEntry(protocol = BookQueries.class, actor = BookQueriesActor.class)"));
    Assert.assertTrue(context.contents().get(17).contains("@DataObjects({AuthorData.class, BookData.class})"));
  }

  private void loadProperties(final CodeGenerationContext context,
                              final StorageType storageType,
                              final DatabaseType databaseType,
                              final ProjectionType projectionType,
                              final Boolean useCQRS) {
    context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp")
            .with(CQRS, useCQRS.toString()).with(DATABASE, databaseType.name())
            .with(COMMAND_MODEL_DATABASE, databaseType.name())
            .with(QUERY_MODEL_DATABASE, databaseType.name())
            .with(STORAGE_TYPE, storageType.name())
            .with(PROJECTION_TYPE, projectionType.name())
            .with(TARGET_FOLDER, HOME_DIRECTORY);
  }

  private void loadContents(final CodeGenerationContext context) {
    context.addContent(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), AUTHOR_STATE_CONTENT_TEXT);
    context.addContent(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), BOOK_STATE_CONTENT_TEXT);
    context.addContent(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), AUTHOR_CONTENT_TEXT);
    context.addContent(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), BOOK_CONTENT_TEXT);
    context.addContent(AGGREGATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorEntity.java"), AUTHOR_ENTITY_CONTENT_TEXT);
    context.addContent(AGGREGATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookEntity.java"), BOOK_ENTITY_CONTENT_TEXT);
    context.addContent(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookRented.java"), BOOK_RENTED_TEXT);
    context.addContent(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), BOOK_PURCHASED_TEXT);
    context.addContent(PROJECTION_DISPATCHER_PROVIDER, new OutputFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT);
    context.addContent(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), AUTHOR_DATA_CONTENT_TEXT);
    context.addContent(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), BOOK_DATA_CONTENT_TEXT);
  }

  private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
  private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
  private static final String MODEL_PACKAGE_PATH =
          Paths.get(PROJECT_PATH, "src", "main", "java",
                  "io", "vlingo", "xoomapp", "model").toString();

  private static final String PERSISTENCE_PACKAGE_PATH =
          Paths.get(PROJECT_PATH, "src", "main", "java",
                  "io", "vlingo", "xoomapp", "infrastructure", "persistence").toString();

  private static final String INFRASTRUCTURE_PACKAGE_PATH =
          Paths.get(PROJECT_PATH, "src", "main", "java",
                  "io", "vlingo", "xoomapp", "infrastructure").toString();

  private static final String  AUTHOR_DATA_CONTENT_TEXT =
          "package io.vlingo.xoomapp.infrastructure; \\n" +
                  "public class AuthorData { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_DATA_CONTENT_TEXT =
          "package io.vlingo.xoomapp.infrastructure; \\n" +
                  "public class BookData { \\n" +
                  "... \\n" +
                  "}";

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

  private static final String AUTHOR_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public interface Author { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public interface Book { \\n" +
                  "... \\n" +
                  "}";

  private static final String AUTHOR_ENTITY_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public interface AuthorEntity { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_ENTITY_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public interface BookEntity { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_RENTED_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public class BookRented extends Event { \\n" +
                  "... \\n" +
                  "}";

  private static final String BOOK_PURCHASED_TEXT =
          "package io.vlingo.xoomapp.model.book; \\n" +
                  "public class BookPurchased extends Event { \\n" +
                  "... \\n" +
                  "}";

  private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT =
          "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                  "public class ProjectionDispatcherProvider { \\n" +
                  "... \\n" +
                  "}";

}
