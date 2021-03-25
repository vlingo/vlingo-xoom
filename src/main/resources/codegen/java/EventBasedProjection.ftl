package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.state.StateStore;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class ${projectionName} extends StateStoreProjectionActor<${dataName}> {

  private static final ${dataName} Empty = ${dataName}.empty();

  public ${projectionName}() {
    this(${storeProviderName}.instance().store);
  }

  public ${projectionName}(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected ${dataName} currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected ${dataName} merge(${dataName} previousData, int previousVersion, ${dataName} currentData, int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    ${dataName} merged = previousData;

    for (final Source<?> event : sources()) {
      switch (${projectionSourceTypesName}.valueOf(event.typeName())) {
      <#list sources as source>
        case ${source.name}: {
          final ${source.name} typedEvent = typed(event);
          <#list source.dataObjectInitializers as initializer>
          ${initializer}
          </#list>
          merged = ${source.dataObjectName}.from(${source.mergeParameters});
          break;
        }

      </#list>
        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }
}
