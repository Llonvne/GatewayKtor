package cn.llonvne.gateway.type

/**
 * 服务订单类。
 *
 * 该类用于表示服务的顺序信息，主要包含一个整型的 `order` 字段，用于确定服务之间的优先级顺序。
 * 实现了 `Comparable<ServiceOrder>` 接口，使得服务订单实例之间可以直接进行比较，便于排序。
 *
 * @property order 订单序号，用于确定服务的执行或处理顺序。
 *
 * @sample compareTo 示例方法展示了如何比较两个服务订单实例的顺序。
 */
data class ServiceOrder(
    val order: Int,
) : Comparable<ServiceOrder> {
    override fun compareTo(other: ServiceOrder): Int = this.order - other.order
}
