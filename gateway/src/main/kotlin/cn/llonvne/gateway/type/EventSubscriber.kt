package cn.llonvne.gateway.type

fun interface EventSubscriber<T> {
    fun subscribe(handler: (T) -> Unit)
}