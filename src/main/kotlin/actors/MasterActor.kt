package actors

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import model.*
import server.StubServer
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MasterActor(
    private val timeout: Long = 1L
) : UntypedAbstractActor(), Delayable {

    private val searchEngineResponses: MutableList<SearchEngineResponse> = ArrayList()
    private val delayableObject: Delayable = DelayableObject()
    private lateinit var requestSender: ActorRef
    private var childNumber = 0

    private fun getChildName() = "child${childNumber++}"

    override fun setDelays(vararg delays: Int) {
        delayableObject.setDelays(*delays)
    }

    override fun nextDelay(): Int {
        return delayableObject.nextDelay()
    }

    override fun onReceive(message: Any?) = when (message) {
        is String -> {
            requestSender = sender
            val timeoutDuration = Duration.create(
                timeout,
                TimeUnit.SECONDS,
            )
            context.system.scheduler().scheduleOnce(
                timeoutDuration,
                self(),
                TimeoutExceeded(timeoutDuration),
                context.dispatcher(),
                self()
            )

            enumValues<SearchEngine>()
                .forEach {
                    val delay = nextDelay()
                    val child = context.actorOf(
                        Props.create(
                            ChildActor::class.java,
                            StubServer(it, RESPONSE_NUMBER, delay)
                        ),
                        getChildName()
                    )

                    child.tell(message, self())
                }

        }

        is SearchEngineResponse -> {
            searchEngineResponses.add(message)

            if (searchEngineResponses.size == SearchEngine.values().size) {
                requestSender.tell(searchEngineResponses, self())
                context.stop(self())
            } else {
            }
        }

        is TimeoutExceeded -> {
            requestSender.tell(searchEngineResponses, self())
            context.stop(self())
        }

        else -> {
            // do nothing
        }
    }


    companion object {
        const val RESPONSE_NUMBER = 5
    }
}