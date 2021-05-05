<#ftl strip_whitespace=true/>
import {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {Link} from "react-router-dom";
import LoadingOrFailed from "../LoadingOrFailed";
import ${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(creatorMethod.name)} from "./${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(creatorMethod.name)}";
<#macro printTableHeaderCell name type>
    <#if valueTypes[type]??>
        <#list valueTypes[type] as subType>
            <@printTableHeaderCell "${name} ${subType.name}" subType.type/>
        </#list>
    <#else>
            <th>${fns.capitalizeMultiWord(name)}</th>
    </#if>
</#macro>
<#macro printTableCell name type>
    <#if valueTypes[type]??>
        <#list valueTypes[type] as subType>
            <@printTableCell "${name}?.${subType.name}" subType.type/>
        </#list>
    <#else>
          <td>{item?.${name}}</td>
    </#if>
</#macro>
<#macro printJSON fields level=0>
  <@compress single_line=true>
    {<#list fields as field>
        ${field.name}: <#if valueTypes[field.type]??><@printJSON valueTypes[field.type] /><#else>''</#if><#if field?has_next>,</#if>
    </#list>}
  </@compress>
</#macro>

const EMPTY_FORM = <@printJSON aggregate.stateFields />;

const ${fns.capitalize(fns.makePlural(aggregate.aggregateName))} = () => {

  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState([]);
  const [currentModal, setCurrentModal] = useState(null);

  const loadItems = useCallback(() => {
    axios.get('${aggregate.api.rootPath}')
      .then(res => res.data)
      .then(data => {
        console.log('${aggregate.aggregateName} axios success', data);
        setItems(data);
      })
      .catch((e) => {
        console.error('${aggregate.aggregateName} axios failed', e);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const onModalActionComplete = useCallback((data) => {
    loadItems();
    setCurrentModal(null);
  }, [loadItems]);

  const ${creatorMethod.name} = useCallback((e) => {
    console.log('showing ${creatorMethod.name} modal');
    setCurrentModal(<${aggregate.aggregateName}${fns.capitalize(creatorMethod.name)} defaultForm={EMPTY_FORM} complete={onModalActionComplete}/>);
  }, [onModalActionComplete]);

  useEffect(() => {
    setLoading(true);
    loadItems();
  }, [loadItems]);

  return (
    <>
      <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 className="h2">${fns.makePlural(aggregate.aggregateName)}</h1>
        <div className="btn-toolbar mb-2 mb-md-0">
          <div className="btn-group me-2">
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={${creatorMethod.name}}>${fns.capitalize(creatorMethod.name)}</button>
          </div>
        </div>
      </div>
      <div>
        {
        items ?
        <table className={'table table-striped table-bordered'}>
        <thead>
          <tr>
          <#list aggregate.stateFields as field>
            <@printTableHeaderCell "${field.name}" "${field.type}" />
          </#list>
          </tr>
        </thead>
        <tbody>
        {items.map(item => (
        <tr key={item.id}>
          <td> <Link to={"${aggregate.api.rootPath}/"+item.id}>{item.id} </Link> </td>
          <#list aggregate.stateFields as field>
            <#if field_index != 0>
              <@printTableCell "${field.name}" "${field.type}" />
            </#if>
          </#list>
        </tr>
        ))}
        </tbody>
        </table>
        : <LoadingOrFailed loading={loading}/>
      }
      </div>

      {currentModal}
    </>
  );
};

export default ${fns.capitalize(fns.makePlural(aggregate.aggregateName))};
