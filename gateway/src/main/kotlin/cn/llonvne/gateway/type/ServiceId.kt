package cn.llonvne.gateway.type

/**
 * 服务标识类。
 *
 * 该类用于唯一标识一个服务，主要通过服务的名称(name)来区分不同的服务实例。
 */
data class ServiceId(
    val name: String,
)
