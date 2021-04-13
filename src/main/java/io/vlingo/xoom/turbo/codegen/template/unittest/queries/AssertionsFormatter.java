// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.unittest.queries;

import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.turbo.codegen.template.unittest.queries.TestDataValueGenerator.TestDataValues;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssertionsFormatter {

  public static List<String> from(final int dataIndex,
                                  final CodeGenerationParameter aggregate,
                                  final List<CodeGenerationParameter> valueObjects,
                                  final TestDataValues testDataValues) {
    final String variableName =
            TestDataFormatter.formatLocalVariableName(dataIndex);

    final List<String> fieldPaths =
            AggregateDetail.resolveFieldsPaths(variableName, aggregate, valueObjects);

    final Function<String, String> mapper =
            fieldPath -> String.format("assertEquals(%s, %s);", fieldPath, testDataValues.retrieve(dataIndex, variableName, fieldPath));

    return fieldPaths.stream().map(mapper).collect(Collectors.toList());
  }

}
