package cn.llonvne.gateway.type

data class ServiceOrder(
    val order: Int,
) : Comparable<ServiceOrder> {
    override fun compareTo(other: ServiceOrder): Int = this.order - other.order
}
