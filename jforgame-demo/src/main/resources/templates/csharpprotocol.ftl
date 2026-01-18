using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

namespace Game.Net.Message
{
    ///<summary>
    /// ${classComment}
    ///</summary>
<#if cmd??>
    [MessageMeta(Cmd = ${cmd})]
</#if>
    public class ${className}<#if cmd??> : ${baseClass}</#if>
    {
    <#list fieldList as field>
        <#if field.desc??>
        /// <summary>
        /// ${field.desc}
        ///  </summary>
        </#if>
        public ${field.type} ${field.name};

    </#list>
    }
}