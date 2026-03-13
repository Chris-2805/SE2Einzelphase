package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_withRank_returnsSurroundingPlayers() {

        val a = GameResult(1, "A", 300, 10.0)
        val b = GameResult(2, "B", 250, 10.0)
        val c = GameResult(3, "C", 200, 10.0)
        val d = GameResult(4, "D", 150, 10.0)
        val e = GameResult(5, "E", 100, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(e, c, a, d, b))

        val res = controller.getLeaderboard(3)

        assertEquals(5, res.size)
    }

    @Test
    fun test_getLeaderboard_invalidRank_throwsException() {

        val a = GameResult(1, "A", 300, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(a))

        try {
            controller.getLeaderboard(0)
        } catch (e: Exception) {
            assertEquals(true, e is org.springframework.web.server.ResponseStatusException)
        }
    }

}