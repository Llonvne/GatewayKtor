package cn.llonvne.service.abc

import cn.llonvne.gateway.config.GatewayYamlConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.process

/**
 * `GatewayServiceBase` 类是基于 [ServiceBase] 实现的抽象类，并实现了 [GatewayService] 接口，
 * 为网关服务提供基础的结构与功能。
 *
 * 此类主要负责处理 [GatewayEvent] 事件，通过收集这些事件并依据配置信息执行相应的业务逻辑。
 * 它确保了 [gatewayEventEmitter] 与 [gatewayYamlConfig] 的初始化，并提供了默认的事件处理流程。
 *
 * 子类应当关注于重写 [collectGateway] 方法以自定义具体的事件处理逻辑，而避免直接修改 [collect] 方法，
 * 以利用类提供的配置初始化和事件初步处理逻辑。
 *
 * 属性:
 *   - `gatewayEventEmitter`: 用于发射 [GatewayEvent] 事件的发射器，由框架设置。
 *   - `gatewayYamlConfig`: 网关的 YAML 配置信息，通过事件处理自动填充。
 */
abstract class GatewayServiceBase :
    ServiceBase(),
    GatewayService {
    protected lateinit var gatewayEventEmitter: Emitter<GatewayEvent>

    final override fun gatewayEventEmitterAware(emitter: Emitter<GatewayEvent>) {
        gatewayEventEmitter = emitter
    }

    protected lateinit var gatewayYamlConfig: GatewayYamlConfig

    /**
     * 收集并处理接收到的 [GatewayEvent] 事件。
     *
     * 此方法首先通过 [process] 函数检查事件是否为 [GatewayConfigAware] 类型，
     * 若匹配则更新 [gatewayYamlConfig] 属性。之后，调用 [collectGateway] 方法
     * 进一步处理特定的 [GatewayEvent]。
     *
     * **注意**：子类应重写 [collectGateway] 方法以实现具体的业务逻辑处理，
     * 而避免直接重写此方法，以确保 [gatewayYamlConfig] 能被正确初始化。
     *
     * @param gatewayEvent 接收到的网关事件实例，用于触发内部处理逻辑。
     */
    final override suspend fun collect(gatewayEvent: GatewayEvent) {
        process<GatewayConfigAware>(gatewayEvent) {
            gatewayYamlConfig = it.config
        }
        collectGateway(gatewayEvent)
    }

    /**
     * 在子类中实现以收集并处理特定的 [GatewayEvent] 事件。
     *
     * 此方法在 [collect] 方法内部被调用，作为处理 [GatewayEvent] 的扩展点。
     * 子类应当根据具体需求重写此方法以执行相应的业务逻辑。
     *
     * [collect] 方法在本类中被 final 修饰以防止子类重写时不调用超类方法导致可能的[NullPointerException]
     *
     * @param e 接收到的 [GatewayEvent] 实例，包含了与网关操作相关的事件信息。
     */
    open suspend fun collectGateway(e: GatewayEvent) {}
}
