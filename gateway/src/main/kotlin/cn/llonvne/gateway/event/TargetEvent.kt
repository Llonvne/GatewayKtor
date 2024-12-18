package cn.llonvne.gateway.event

import cn.llonvne.service.Service
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

interface TargetEvent<T : Service> {
    val target: KClass<T>

    fun isTarget(service: Service): Boolean {
        return service::class.isSubclassOf(target)
    }
}