// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.http.Method.*;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.*;

public class RouteDetail {

  private static final String BODY_DEFAULT_NAME = "data";
  private static final String METHOD_PARAMETER_PATTERN = "final %s %s";
  private static final String METHOD_SIGNATURE_PATTERN = "%s(%s)";
  private static final List<Method> BODY_SUPPORTED_HTTP_METHODS = Arrays.asList(POST, PUT, PATCH);

  public static String resolveBodyName(final CodeGenerationParameter route) {
    final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

    if (!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
      return "";
    }

    if (route.hasAny(BODY)) {
      return route.retrieveRelatedValue(BODY);
    }

    return BODY_DEFAULT_NAME;
  }

  public static String resolveBodyType(final CodeGenerationParameter route) {
    final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

    if (!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
      return "";
    }

    if (route.parent().isLabeled(AGGREGATE)) {
      return AnnotationBasedTemplateStandard.DATA_OBJECT.resolveClassname(route.parent(AGGREGATE).value);
    }

    return route.retrieveRelatedValue(BODY_TYPE);
  }

  public static String resolveMethodSignature(final CodeGenerationParameter routeSignature) {
    if (hasValidMethodSignature(routeSignature.value)) {
      return routeSignature.value;
    }

    if (routeSignature.retrieveRelatedValue(Label.ROUTE_METHOD, Method::from).isGET()) {
      final Stream<CodeGenerationParameter> parameters =
              routeSignature.retrieveAllRelated(METHOD_PARAMETER);

      final String arguments = parameters.map(field -> {
        final String fieldType = FieldDetail.typeOf(field.parent(Label.AGGREGATE), field.value);
        return String.format("final %s %s", fieldType, field.value);
      }).collect(Collectors.joining(", "));

      return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, arguments);
    }

    return resolveMethodSignatureWithParams(routeSignature);
  }

  private static String resolveMethodSignatureWithParams(final CodeGenerationParameter routeSignature) {
    final String idParameter =
            routeSignature.retrieveRelatedValue(REQUIRE_ENTITY_LOADING, Boolean::valueOf) ?
                    String.format(METHOD_PARAMETER_PATTERN, "String", "id") : "";

    final CodeGenerationParameter method = AggregateDetail.methodWithName(routeSignature.parent(), routeSignature.value);
    final String dataClassname = AnnotationBasedTemplateStandard.DATA_OBJECT.resolveClassname(routeSignature.parent().value);
    final String dataParameterDeclaration = String.format(METHOD_PARAMETER_PATTERN, dataClassname, "data");
    final String dataParameter = method.hasAny(METHOD_PARAMETER) ? dataParameterDeclaration : "";
    final String parameters =
            Stream.of(idParameter, dataParameter).filter(param -> !param.isEmpty())
                    .collect(Collectors.joining(", "));
    return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, parameters);
  }

  private static boolean hasValidMethodSignature(final String signature) {
    return signature.contains("(") && signature.contains(")");
  }

}
