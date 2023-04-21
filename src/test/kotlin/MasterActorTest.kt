import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.Creator
import akka.pattern.PatternsCS
import akka.util.Timeout
import actors.MasterActor
import model.AggregatorResponse
import model.SearchEngine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
class MasterActorTest {

    companion object {
        const val NUMBER_OF_ENGINES = 3
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
    fun testMasterActorWithNoDelay() {
        val masterActor = createMasterActor(1L)
        val response = getAggregatorResponse(
            masterActor,
            "some query"
        )

        assertEquals(NUMBER_OF_ENGINES, response.size)
        checkFullResponse(response)
    }

    @Test
    fun testMasterActorWithBigTimeoutAndSmallDelay() {
        val masterActor = createMasterActor(6L, 2, 2, 2)
        val response = getAggregatorResponse(
            masterActor,
            "some query for small delay"
        )

        assertEquals(NUMBER_OF_ENGINES, response.size)
        checkFullResponse(response)
    }

    @Test
    fun testMasterActorWithMediumDelay() {
        val masterActor = createMasterActor(6L, 2, 4, 7)
        val response = getAggregatorResponse(
            masterActor,
            "some query for medium delay"
        )

        assertEquals(NUMBER_OF_ENGINES - 1, response.size)
    }

    @Test
    fun testMasterActorWithBigDelay() {
        val masterActor = createMasterActor(6L, 4, 10, 10)
        val response = getAggregatorResponse(
            masterActor,
            "some query for big delay"
        )

        assertEquals(NUMBER_OF_ENGINES - 2, response.size)
    }

    @Test
    fun testMasterActorWithVeryBigDelay() {
        val masterActor = createMasterActor(6L, 7, 8, 9)
        val response = getAggregatorResponse(
            masterActor,
            "some query for big delay"
        )

        assertEquals(NUMBER_OF_ENGINES - 3, response.size)
    }

    private fun checkFullResponse(response: AggregatorResponse) {
        enumValues<SearchEngine>().forEach {
            assertTrue(response.map { obj -> obj.searchEngine }.contains(it))
        }
    }

    private fun getAggregatorResponse(masterActor: ActorRef, name: String): AggregatorResponse {
        return PatternsCS.ask(
            masterActor,
            name,
            Timeout.apply(40, TimeUnit.SECONDS)
        ).toCompletableFuture().join() as AggregatorResponse
    }

    private fun createMasterActor(timeout: Long, vararg delays: Int): ActorRef {
        return system.actorOf(Props.create(Creator {
            val masterActorCreator = MasterActor(timeout)
            masterActorCreator.setDelays(*delays)
            return@Creator masterActorCreator
        }))
    }

}