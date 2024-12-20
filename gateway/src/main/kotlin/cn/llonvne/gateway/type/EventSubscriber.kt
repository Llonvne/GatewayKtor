package cn.llonvne.gateway.type

/**
 * 事件订阅者接口。
 *
 * 此接口定义了一个通用的订阅方法，允许以协程方式注册处理特定类型事件的回调函数。
 * 任何实现了此接口的类或对象都能够订阅并处理匹配指定泛型类型 `T` 的事件。
 *
 * @param T 事件类型，表示订阅者感兴趣的事件的数据类型。
 *
 * @receiver [subscribe] 方法接收一个挂起函数作为参数，该函数会在事件触发时被调用，
 * 其中参数为实际发生的事件实例。
 */
fun interface EventSubscriber<T> {
    fun subscribe(handler: suspend (T) -> Unit)
}
