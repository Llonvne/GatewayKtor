package cn.llonvne.service.abc

import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.type.Emitter

/**
 * 接口ApiEventEmitterAware表示一个组件具有对API事件发射器感知的能力。
 * 实现此接口的类能够接收[Emitter]实例，该实例专门用于发射[ApiEvent]类型的事件。
 *
 * 具体服务实现通常无需关心该接口，使用 [ServiceBase.apiEventEmitter] 可直接发射[ApiEvent]事件
 *
 * @see Emitter
 * @see ApiEvent
 */
interface ApiEventEmitterAware {
    fun apiEventEmitterAware(emitter: Emitter<ApiEvent>)
}
