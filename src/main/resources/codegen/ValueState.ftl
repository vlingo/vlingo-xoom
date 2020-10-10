package ${packageName};

public final class ${stateName} {

  <#list members as member>
  ${member}
  </#list>

  public static ${stateName} identifiedBy(final ${idType} id) {
    return new ${stateName}(${nullableParameters});
  }

  public ${stateName} (${methodParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

  <#list methods as method>
  ${method}
  </#list>

}
