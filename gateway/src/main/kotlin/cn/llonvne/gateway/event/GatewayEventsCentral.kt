package cn.llonvne.gateway.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GatewayEventsCentral(
    val scope: CoroutineScope,
) {
    private val gatewayEventFlow = MutableSharedFlow<GatewayEvent>()

    private val serviceEventFlow = MutableSharedFlow<ServiceEvent>()

    private val apiEventFlow = MutableSharedFlow<ApiEvent>()

    fun emit(event: GatewayEvent) {
        scope.launch { gatewayEventFlow.emit(event) }
    }

    @JvmName("collectGatewayEvent")
    fun collect(collector: FlowCollector<GatewayEvent>) {
        scope.launch { gatewayEventFlow.collect(collector) }
    }

    fun emit(event: ServiceEvent) {
        scope.launch { serviceEventFlow.emit(event) }
    }

    @JvmName("collectServiceEvent")
    fun collect(collector: FlowCollector<ServiceEvent>) {
        scope.launch { serviceEventFlow.collect(collector) }
    }

    fun emit(event: ApiEvent) {
        scope.launch { apiEventFlow.emit(event) }
    }

    @JvmName("collectApiEvent")
    fun collect(collector: FlowCollector<ApiEvent>) {
        scope.launch { apiEventFlow.collect(collector) }
    }


}
