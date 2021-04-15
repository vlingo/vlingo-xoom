package ${packageName};

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
<#list imports as import>
import ${import.qualifiedClassName};
</#list>
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ${entityUnitTestName} {

  private World world;
  private Journal<String> journal;
  private ${dispatcherName} dispatcher;
  private ${aggregateProtocolName} ${aggregateProtocolVariable};

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new ${dispatcherName}();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    <#list sourcedEvents as event>
    entryAdapterProvider.registerAdapter(${event.name}.class, new ${event.adapterName}());
    </#list>

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, ${entityName}.class, ${entityName}.class.getSimpleName()));

    ${aggregateProtocolVariable} = world.actorFor(${aggregateProtocolName}.class, ${entityName}.class, "#1");
  }

  <#list testCases as testCase>
  <#list testCase.dataDeclarations as dataDeclaration>
  ${dataDeclaration}
  </#list>

  @Test
  public void ${testCase.methodName}() {
    <#list testCase.preliminaryStatements as statement>
    ${statement}
    </#list>
    ${testCase.resultAssignmentStatement}

    <#list testCase.assertions as assertion>
    ${assertion}
    </#list>
  }

  </#list>
  <#if auxiliaryEntityCreation.required>
  <#list auxiliaryEntityCreation.dataDeclarations as dataDeclaration>
  ${dataDeclaration}
  </#list>

  private void ${auxiliaryEntityCreation.methodName}() {
    ${auxiliaryEntityCreation.statement}
  }
  </#if>
}
