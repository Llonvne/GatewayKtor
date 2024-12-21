package cn.llonvne.service.abc

import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.type.Emitter

/**
 * 标记接口，表示该类或接口具有对接 GatewayEvent 事件发射器的感知能力。
 *
 * 实现此接口的类需要提供一个方法 `gatewayEventEmitterAware`，该方法接收一个 [Emitter] 实例，
 * 参数类型为 [GatewayEvent]。这使得实现类能够在适当的时机被通知并关联到一个 [GatewayEvent] 事件的发射器，
 * 以便它可以发送或订阅此类事件，从而与其他组件进行通信。
 *
 * 具体服务实现通常无需关心该接口，使用 [GatewayServiceBase.gatewayEventEmitter] 可直接发射[GatewayEvent]事件
 *
 * @see Emitter
 * @see GatewayEvent
 */
interface GatewayEventEmitterAware {
    fun gatewayEventEmitterAware(emitter: Emitter<GatewayEvent>)
}
