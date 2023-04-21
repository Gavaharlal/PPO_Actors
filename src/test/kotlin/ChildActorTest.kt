import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.PatternsCS
import akka.util.Timeout
import actors.ChildActor
import model.SearchEngine
import model.SearchEngineResponse
import server.StubServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ChildActorTest {

    companion object {
        const val RESPONSE_NUMBER = 15
        val SEARCH_ENGINE = SearchEngine.YANDEX
    }

    private lateinit var system: ActorSystem

    @Before
    fun setUp() {
        system = ActorSystem.create("TestSystem")
    }

    @After
    fun cleanUp() {
        system.terminate()
    }

    @Test
    fun testChildActor() {
        val childActor = system.actorOf(
            Props.create(
                ChildActor::class.java,
                StubServer(SEARCH_ENGINE, RESPONSE_NUMBER)
            )
        )
        val response = PatternsCS.ask(
            childActor,
            "some_query",
            Timeout.apply(5, TimeUnit.SECONDS)
        ).toCompletableFuture().join() as SearchEngineResponse

        assertEquals(RESPONSE_NUMBER, response.results.size)
        assertEquals(SEARCH_ENGINE, response.searchEngine)
    }
}