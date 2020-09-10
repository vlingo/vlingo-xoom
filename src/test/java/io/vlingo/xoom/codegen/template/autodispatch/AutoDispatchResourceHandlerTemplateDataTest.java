// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.storage.QueriesParameter;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;

public class AutoDispatchResourceHandlerTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final CodeGenerationContext context =
                CodeGenerationContext.with(loadParameters())
                        .addContent(STORE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"), QUERY_MODEL_STORE_PROVIDER_CONTENT);

        final List<TemplateData> templatesData =
                AutoDispatchResourceHandlerTemplateData.from(context);

        Assert.assertEquals(2, templatesData.size());

        assertAuthorResource(templatesData);
        assertBookResource(templatesData);
    }

    private void assertAuthorResource(final List<TemplateData> templatesData) {
        final TemplateData resourceHandlerData =
                templatesData.stream().filter(data -> data.filename().endsWith("AuthorResourceHandler.java")).findFirst().get();

        Assert.assertEquals("/authors", resourceHandlerData.parameters().find(TemplateParameter.URI_ROOT));
        Assert.assertEquals("io.vlingo.xoomapp.resources", resourceHandlerData.parameters().find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorResourceHandler", resourceHandlerData.parameters().find(TemplateParameter.REST_RESOURCE_NAME));

        final QueriesParameter queriesParameter =
                resourceHandlerData.parameters().find(TemplateParameter.QUERIES);

        Assert.assertEquals("AuthorQueries", queriesParameter.getProtocolName());
        Assert.assertEquals("AuthorQueriesActor", queriesParameter.getActorName());
        Assert.assertEquals("authorQueries", queriesParameter.getAttributeName());

        final List<RouteDeclarationParameter> routeDeclarationParameters =
                resourceHandlerData.parameters().find(TemplateParameter.ROUTE_DECLARATIONS);

        Assert.assertEquals(2, routeDeclarationParameters.size());

        final RouteDeclarationParameter nameUpdateRouteDeclaration =
                routeDeclarationParameters.stream().filter(parameter ->
                        parameter.getHandlerName().equals("changeAuthorName"))
                        .findFirst().get();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.AuthorData", nameUpdateRouteDeclaration.getBodyType());
        Assert.assertEquals("patch", nameUpdateRouteDeclaration.getBuilderMethod());
        Assert.assertEquals("/authors/{authorId}/name", nameUpdateRouteDeclaration.getPath());
        Assert.assertEquals(1, nameUpdateRouteDeclaration.getParameterTypes().size());
        Assert.assertEquals("String", nameUpdateRouteDeclaration.getParameterTypes().get(0));

        Assert.assertEquals(2, resourceHandlerData.dependencies().size());

        final TemplateData queryRouteMethodTemplateData =
                resourceHandlerData.dependencies().stream()
                        .filter(data -> data.parameters().find(TemplateParameter.ROUTE_SIGNATURE)
                                        .equals("queryById(final String authorId)"))
                        .findFirst().get();

        Assert.assertEquals("authorId", queryRouteMethodTemplateData.parameters().find(TemplateParameter.ID_NAME));
        Assert.assertEquals("GET", queryRouteMethodTemplateData.parameters().find(TemplateParameter.ROUTE_METHOD));
        Assert.assertEquals("authorOf(authorId)", queryRouteMethodTemplateData.parameters().find(TemplateParameter.ROUTE_HANDLER));
        Assert.assertEquals("io.vlingo.xoomapp.model.Author", queryRouteMethodTemplateData.parameters().find(TemplateParameter.MODEL_PROTOCOL));
        Assert.assertEquals("authorQueries", queryRouteMethodTemplateData.parameters().find(TemplateParameter.QUERIES_ATTRIBUTE));
        Assert.assertEquals("", queryRouteMethodTemplateData.parameters().find(TemplateParameter.ADAPTER_INVOCATION));
    }

    private void assertBookResource(final List<TemplateData> templatesData) {
        final TemplateData resourceHandlerData =
                templatesData.stream().filter(data -> data.filename().endsWith("BookResourceHandler.java")).findFirst().get();

        Assert.assertEquals("/books", resourceHandlerData.parameters().find(TemplateParameter.URI_ROOT));
        Assert.assertEquals("io.vlingo.xoomapp.resources", resourceHandlerData.parameters().find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("BookResourceHandler", resourceHandlerData.parameters().find(TemplateParameter.REST_RESOURCE_NAME));

        final QueriesParameter queriesParameter =
                resourceHandlerData.parameters().find(TemplateParameter.QUERIES);

        Assert.assertEquals("BookQueries", queriesParameter.getProtocolName());
        Assert.assertEquals("BookQueriesActor", queriesParameter.getActorName());
        Assert.assertEquals("bookQueries", queriesParameter.getAttributeName());

        final List<RouteDeclarationParameter> routeDeclarationParameters =
                resourceHandlerData.parameters().find(TemplateParameter.ROUTE_DECLARATIONS);

        Assert.assertEquals(1, routeDeclarationParameters.size());
        Assert.assertEquals(1, resourceHandlerData.dependencies().size());
    }

    private CodeGenerationParameters loadParameters() {
        final CodeGenerationParameter firstAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "changeAuthorName(final String authorId, final AuthorData authorData)")
                        .relate(ROUTE_HANDLER, "changeName(authorData.name)")
                        .relate(ROUTE_PATH, "/authors/{authorId}/name")
                        .relate(ROUTE_METHOD, "PATCH")
                        .relate(CUSTOM_ROUTE, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String")
                        .relate(BODY, "authorData")
                        .relate(BODY_TYPE, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(RESPONSE_DATA, "AuthorData.from");

        final CodeGenerationParameter secondAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryById(final String authorId)")
                        .relate(ROUTE_HANDLER, "authorOf(authorId)")
                        .relate(ROUTE_PATH, "/authors/{authorId}")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(CUSTOM_ROUTE, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String");

        final CodeGenerationParameter authorResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.AuthorResource")
                        .relate(URI_ROOT, "/authors")
                        .relate(MODEL_PROTOCOL, "io.vlingo.xoomapp.model.Author")
                        .relate(MODEL_ACTOR, "io.vlingo.xoomapp.model.AuthorEntity")
                        .relate(MODEL_DATA, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueriesActor")
                        .relate(firstAuthorRouteParameter).relate(secondAuthorRouteParameter);

        final CodeGenerationParameter bookRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryBooks()")
                        .relate(ROUTE_HANDLER, "retrieveAll()")
                        .relate(ROUTE_PATH, "/books")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(CUSTOM_ROUTE, "false");

        final CodeGenerationParameter bookResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.BookResource")
                        .relate(URI_ROOT, "/books").relate(bookRouteParameter)
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.BookQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.BookQueriesActor");

        return CodeGenerationParameters.from(authorResourceParameter, bookResourceParameter);
    }

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

    private static final String QUERY_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class QueryModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

}
