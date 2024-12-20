package cn.llonvne.service

import cn.llonvne.gateway.config.GatewayDbConfig
import cn.llonvne.service.abc.GatewayDbService

class PostgreSqlService : GatewayDbService {
    override val name: String = "PostgreSqlService"

    override suspend fun processDb(config: GatewayDbConfig) {
        TODO()
    }
}