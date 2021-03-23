package ${packageName};

import java.util.List;
import java.util.stream.Collectors;
<#if imports?has_content>
<#list imports as import>
import ${import.qualifiedClassName};
</#list>
</#if>

public class ${dataName} {
  <#list members as member>
  ${member}
  </#list>

  <#list staticFactoryMethods as factoryMethod>
  public static ${factoryMethod.dataObjectName} from(${factoryMethod.parameters}) {
    return ${factoryMethod.constructorInvocation};
  }

  </#list>
  public static List<${dataName}> from(final List<${stateName}> states) {
    return states.stream().map(${dataName}::from).collect(Collectors.toList());
  }

  public static ${dataName} empty() {
    return from(${stateName}.identifiedBy(""));
  }

  private ${dataName} (${constructorParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

}
