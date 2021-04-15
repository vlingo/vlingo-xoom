// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen;

import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.turbo.codegen.template.storage.Model;
import io.vlingo.xoom.turbo.codegen.template.storage.StorageType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.template.Template.*;

public class CodeGenerationSetup {

  public static final String DATA_SCHEMA_CATEGORY = "data";
  public static final String EVENT_SCHEMA_CATEGORY = "event";
  public static final String DEFAULT_SCHEMA_VERSION = "1.0.0";

  public static final List<String> KOTLIN_RESERVED_WORDS = Arrays.asList("object", "public", "get", "set");
  public static final List<String> SCALAR_NUMERIC_TYPES = Arrays.asList("byte", "short", "int", "long", "double");
  public static final List<String> SCALAR_TYPES = Stream.of(SCALAR_NUMERIC_TYPES, Arrays.asList("boolean", "string"))
                  .flatMap(List::stream).collect(Collectors.toList());

  @SuppressWarnings("serial")
    public static final Map<StorageType, String> AGGREGATE_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<StorageType, String>(){{
                        //put(StorageType.OBJECT_STORE, OBJECT_ENTITY.filename);
                        put(StorageType.STATE_STORE, STATEFUL_ENTITY.filename);
                        put(StorageType.JOURNAL, EVENT_SOURCE_ENTITY.filename);
                    }}
            );

  @SuppressWarnings("serial")
    public static final Map<StorageType, String> AGGREGATE_METHOD_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<StorageType, String>(){{
                        //put(StorageType.OBJECT_STORE, "");
                        put(StorageType.STATE_STORE, STATEFUL_ENTITY_METHOD.filename);
                        put(StorageType.JOURNAL, EVENT_SOURCE_ENTITY_METHOD.filename);
                    }}
            );

  @SuppressWarnings("serial")
    public static final Map<StorageType, String> ADAPTER_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<StorageType, String>(){{
                        //put(StorageType.OBJECT_STORE, "");
                        put(StorageType.STATE_STORE, STATE_ADAPTER.filename);
                        put(StorageType.JOURNAL, ENTRY_ADAPTER.filename);
                    }}
            );

  @SuppressWarnings("serial")
    public static final Map<ProjectionType, String> PROJECTION_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<ProjectionType, String>(){{
                        put(ProjectionType.EVENT_BASED, EVENT_BASED_PROJECTION.filename);
                        put(ProjectionType.OPERATION_BASED, OPERATION_BASED_PROJECTION.filename);
                    }}
            );

  @SuppressWarnings("serial")
    private static final Map<StorageType, String> COMMAND_MODEL_STORE_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<StorageType, String>(){{
                        //put(StorageType.OBJECT_STORE, OBJECT_STORE_PROVIDER.filename);
                        put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.JOURNAL, JOURNAL_PROVIDER.filename);
                    }}
            );

  @SuppressWarnings("serial")
    private static final Map<StorageType, String> QUERY_MODEL_STORE_TEMPLATES =
            Collections.unmodifiableMap(
                    new HashMap<StorageType, String>(){{
                        //put(StorageType.OBJECT_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.JOURNAL, STATE_STORE_PROVIDER.filename);
                    }}
            );


  public static Map<StorageType, String> storeProviderTemplatesFrom(final Model model) {
        if(model.isQueryModel()) {
            return QUERY_MODEL_STORE_TEMPLATES;
        }
        return COMMAND_MODEL_STORE_TEMPLATES;
    }

}
