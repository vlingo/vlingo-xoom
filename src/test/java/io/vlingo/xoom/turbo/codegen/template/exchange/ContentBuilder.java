// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;

public class ContentBuilder {

    public static Content authorContent() {
        return Content.with(AGGREGATE_PROTOCOL, new OutputFile("", "Author.java"), null, null, AUTHOR_CONTENT_TEXT);
    }

    public static Content authorEntityContent() {
        return Content.with(AGGREGATE, new OutputFile("", "AuthorEntity.java"), null, null, AUTHOR_ENTITY_CONTENT_TEXT);
    }
    
    public static Content bookContent() {
        return Content.with(AGGREGATE_PROTOCOL, new OutputFile("", "Book.java"), null, null, BOOK_CONTENT_TEXT);
    }

    public static Content authorDataObjectContent() {
        return Content.with(DATA_OBJECT, new OutputFile("", "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT);
    }

    public static Content authorRatedEvent() {
        return Content.with(DOMAIN_EVENT, new OutputFile("", "AuthorRated.java"), null, null, AUTHOR_RATED_CONTENT_TEXT);
    }

    public static Content authorBlockedEvent() {
        return Content.with(DOMAIN_EVENT, new OutputFile("", "AuthorBlocked.java"), null, null, AUTHOR_BLOCKED_CONTENT_TEXT);
    }

    public static Content bookSoldOutEvent() {
        return Content.with(DOMAIN_EVENT, new OutputFile("", "BookSoldOut.java"), null, null, BOOK_SOLD_OUT_CONTENT_TEXT);
    }

    public static Content bookPurchasedEvent() {
        return Content.with(DOMAIN_EVENT, new OutputFile("", "BookPurchased.java"), null, null, BOOK_PURCHASED_CONTENT_TEXT);
    }

    public static Content rankValueObject() {
        return Content.with(VALUE_OBJECT, new OutputFile("", "Rank.java"), null, null, RANK_VALUE_OBJECT_CONTENT_TEXT);
    }

    public static Content nameValueObject() {
        return Content.with(VALUE_OBJECT, new OutputFile("", "Name.java"), null, null, NAME_VALUE_OBJECT_CONTENT_TEXT);
    }

    public static List<Content> contents() {
        return Arrays.asList(authorContent(), authorEntityContent(), bookContent(), authorDataObjectContent(),
                authorRatedEvent(), authorBlockedEvent(), bookSoldOutEvent(), bookPurchasedEvent(),
                rankValueObject(), nameValueObject());
    }

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
                    "public class AuthorEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_RATED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorRated extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_BLOCKED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorRated extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_SOLD_OUT_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookSoldOut extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_PURCHASED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookPurchased extends DomainEvent { \\n" +
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
}
