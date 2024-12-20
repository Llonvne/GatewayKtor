package cn.llonvne.service.abc

import cn.llonvne.gateway.Constants
import cn.llonvne.gateway.config.GatewayDbConfig
import cn.llonvne.gateway.config.GatewayRedisConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.type.ServiceOrder
import cn.llonvne.service.process

/**
 * 提供网关持久化服务的接口定义，继承自 [GatewayService]。
 * 实现此接口的类需处理与数据持久化相关的 [GatewayEvent]。
 *
 * @property order 此接口继承自 [GatewayService]，其 `order` 属性默认值为 [Constants.ServiceOrderConstants.persistenceServiceOrder]，
 *                 用以标识服务执行的顺序。
 */
sealed interface GatewayPersistenceService : GatewayService {
    override val order: ServiceOrder get() = Constants.ServiceOrderConstants.persistenceServiceOrder

    override suspend fun collect(gatewayEvent: GatewayEvent) = process<GatewayConfigAware>(gatewayEvent) {
        when (this) {
            is GatewayRedisService -> processRedis(it.config.persistence.redis)
            is GatewayDbService -> processDb(it.config.persistence.db)
        }
    }
}

/**
 * 提供基于Redis的数据处理服务，继承自[GatewayPersistenceService]。
 * 该接口用于处理与Redis相关的数据持久化逻辑，特别是针对网关配置中的Redis配置部分。
 *
 * @property processRedis 用于处理具体Redis配置的挂起函数，接收一个[GatewayRedisConfig]实例作为参数。
 */
interface GatewayRedisService : GatewayPersistenceService {
    suspend fun processRedis(config: GatewayRedisConfig)
}

/**
 * 数据库持久化服务接口。
 *
 * 扩展自 [GatewayPersistenceService] 接口，专门用于处理与数据库相关的持久化操作。
 * 实现该接口的类需要提供对数据库配置的处理能力，以便于网关系统能够与数据库交互，
 * 完成如配置存储、日志记录或其他必要的数据持久化任务。
 *
 * 主要方法：
 * - [processDb]：异步方法，用于处理数据库配置，根据给定的 [GatewayDbConfig] 实例执行具体的操作。
 *
 * @see GatewayPersistenceService 网关持久化服务基础接口。
 * @see GatewayDbConfig 数据库配置详情。
 */
interface GatewayDbService : GatewayPersistenceService {
    suspend fun processDb(config: GatewayDbConfig)
}