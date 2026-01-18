<#list importItems as child>
import ${child} from "./item/${child}";
</#list>
   <#list importItems2 as child>
import ${child} from "./${child}";
</#list>
/**
 * ${classComment}
*/
export default class ${className}<#if cmd??> extends ${baseClass}</#if> {
    <#if cmd??>
    public static cmd:number = ${cmd};
    </#if>
    <#list fieldList as field>
    <#if field.desc??>
    // ${field.desc}
    </#if>
    public ${field.name}: ${field.type};
    </#list>
}