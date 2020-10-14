package com.snqu.shopping.util.ext

import kotlin.properties.Delegates

/**
 * 定义一个属性委托于Delegates.vetoable方法返回的ReadWriteProperty对象
 * Delegates.vetoable满足条件才能修改成功
 */
class ListenerPropertyClass {

    var listenerProperty: Int by Delegates.vetoable(0, { property, oldValue, newValue ->
        newValue >= 0//满足条件修改成功
    })
}