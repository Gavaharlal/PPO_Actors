@file:Suppress("UNCHECKED_CAST")

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.PatternsCS
import akka.util.Timeout
import actors.MasterActor
import model.AggregatorResponse
import java.util.*
import java.util.concurrent.TimeUnit

fun main() {
    val scanner = Scanner(System.`in`)
    val system = ActorSystem.create()
    var masterNumber = 0

    while (true) {
        print("Enter request: ")

        val query: String = scanner.next()
        if (query == "\\q") {
            break
        }

        val master: ActorRef = system.actorOf(
            Props.create(MasterActor::class.java),
            "master${masterNumber++}"
        )
        PatternsCS.ask(master, query, Timeout.apply(15, TimeUnit.SECONDS))
            .toCompletableFuture()
            .thenAccept {
                val res = it as AggregatorResponse
                println(res)
            }
    }
    system.terminate()

}
