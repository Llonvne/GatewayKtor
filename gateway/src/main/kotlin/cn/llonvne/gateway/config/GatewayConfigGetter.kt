package cn.llonvne.gateway.config

fun interface GatewayConfigGetter {
    fun get(): GatewayYamlConfig
}
