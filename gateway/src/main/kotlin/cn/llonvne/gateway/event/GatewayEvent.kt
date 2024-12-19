package cn.llonvne.gateway.event

/**
 * 表示对于 Gateway 管理的事件
 */
sealed interface GatewayEvent

sealed interface EmitterAware<E : GatewayEvent> {
    fun emit(e: E)
}





