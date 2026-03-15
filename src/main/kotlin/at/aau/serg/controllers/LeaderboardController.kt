package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        // Holt die gespeicherten Spielergebnisse vom Service

        val sortedResults = gameResultService.getGameResults()
            .sortedWith(compareByDescending<GameResult> { it.score }.thenBy { it.timeInSeconds })
        //Score wird absteigend verglichen, höherer Score weiter oben, niedriger Score unten
        //wenn gleicher Score, dann wird die bessere Zeit, also die kleinere Zeit genommen

        if (rank == null) {
            return sortedResults
        }
        // Wenn kein rank-Parameter übergeben wurde,
        // wird einfach das komplette Leaderboard zurückgegeben

        if (rank < 1 || rank > sortedResults.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }
        // Überprüfung auf ungültige Rank-Werte
        // Rank muss mindestens 1 sein und darf nicht größer sein als die Anzahl der Einträge


        val index = rank - 1
        // Listen in Kotlin beginnen bei Index 0,
        // während die Rangliste bei 1 beginnt.
        // Deshalb wird der Rang auf einen Listenindex umgerechnet.


        val fromIndex = maxOf(0, index - 3)
        //Berechnet den Startindex der Teilmenge.
        // Es sollen bis zu 3 Spieler vor dem gewünschten Rang angezeigt werden.
        // maxOf verhindert negative Indizes am Anfang der Liste.

        val toIndex = minOf(sortedResults.size, index + 4)
        // Berechnet den Endindex der Teilmenge.
        // Es sollen bis zu 3 Spieler nach dem gewünschten Rang angezeigt werden.
        // minOf verhindert, dass über das Ende der Liste hinausgegriffen wird.


        return sortedResults.subList(fromIndex, toIndex)
        // Gibt nur den berechneten Teilbereich des Leaderboards zurück.
        // subList(start, end) enthält start (inklusive) bis end (exklusive).
    }

}