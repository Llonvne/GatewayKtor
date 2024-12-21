package cn.llonvne.service

import cn.llonvne.gateway.ServiceInsight

data class ServiceInsightWithAction(
    val name: String,
    val baseUri: String,
    val namespace: String,
    val apis: List<ApiDescriptorWithAction>,
) {
    fun toServiceInsight(): ServiceInsight = ServiceInsight(name, baseUri, namespace, apis.map { it.apiDescriptor })
}