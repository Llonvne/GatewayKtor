package cn.llonvne.gateway.config

import net.mamoe.yamlkt.Yaml

class GatewayConfigYamlGetter(
    var configFilename: String,
) : GatewayConfigGetter {
    override fun get(): GatewayYamlConfig =
        Yaml.decodeFromString(
            GatewayYamlConfig.serializer(),
            this.javaClass.classLoader
                .getResourceAsStream(configFilename)
                ?.bufferedReader(Charsets.UTF_8)
                ?.readText()!!,
        )
}
