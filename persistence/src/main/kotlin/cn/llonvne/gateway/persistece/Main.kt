package cn.llonvne.gateway.persistece

import cn.llonvne.gateway.ApiDescriptor
import cn.llonvne.gateway.HttpMethod
import cn.llonvne.service.ApiDescriptorWithAction
import cn.llonvne.service.KitImpl
import cn.llonvne.service.ServiceInsightWithAction
import hiberante7.Hibernate
import io.ktor.server.response.respondText

suspend fun main() {

    Hibernate.run(arrayOf())



    KitImpl(
        "localhost",
        8080,
        8082,
        ServiceInsightWithAction(
            "Persistence", "/api", "/persistence", listOf(
                ApiDescriptorWithAction(
                    ApiDescriptor(
                        "persistBook",
                        "/persistBook",
                        "persistBook",
                        "persist a book",
                        HttpMethod.POST,
                        "plain/text"
                    )
                ) {
                    call.respondText { "Hello Persist book " }
                }
            ))
    ).start()
}