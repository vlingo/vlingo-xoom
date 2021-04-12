
      <plugin>
        <groupId>io.vlingo.xoom</groupId>
        <artifactId>xoom-build-plugins</artifactId>
        <version>${vlingo.xoom.version}</version>
        <executions>
          <#if hasProducerExchange>
<execution>
            <goals>
              <goal>push-schemata</goal>
            </goals>
            <configuration>
              <srcDirectory>${r"${basedir}"}/src/main/vlingo/schemata</srcDirectory>
              <schemataService>
                <url>http://localhost:9019</url>
                <clientOrganization>${producerOrganization}</clientOrganization>
                <clientUnit>${producerUnit}</clientUnit>
                <hierarchicalCascade>true</hierarchicalCascade>
              </schemataService>
              <schemata>
                <#list producerSchemas as schema>
                <schema>
                  <ref>${schema.reference}</ref>
                  <src>${schema.file}</src>
                </schema>
                </#list>
              </schemata>
            </configuration>
          </execution>
          </#if>
          <#if hasConsumerExchange>
          <execution>
            <id>pullSchemata</id>
            <goals>
              <goal>pull-schemata</goal>
            </goals>
            <configuration>
              <schemataService>
                <url>http://localhost:9019</url>
                <clientOrganization>Inform the client organization</clientOrganization>
                <clientUnit>Inform the client unit</clientUnit>
              </schemataService>
              <schemata>
                <#list consumerSchemas as schema>
                <schema>
                  <ref>${schema.reference}</ref>
                </schema>
                </#list>
              </schemata>
            </configuration>
          </execution>
          </#if>
        </executions>
      </plugin>
