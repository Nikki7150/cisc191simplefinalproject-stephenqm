package edu.sdccd.cisc191.game.service;

import edu.sdccd.cisc191.game.dto.CreateMatchRequest;
import edu.sdccd.cisc191.game.dto.GameMatchResponse;
import edu.sdccd.cisc191.game.dto.LeaderboardEntry;
import edu.sdccd.cisc191.game.exception.InvalidMatchException;
import edu.sdccd.cisc191.game.model.GameMatch;
import edu.sdccd.cisc191.game.repository.GameMatchRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameMatchRepository repository;
    private final Random random = new Random();

    // MODULE 1: 2D array storing last 10 match scores [playerOne, playerTwo]
    private final int[][] scoreHistory = new int[10][2];
    private int scoreCount = 0;

    // MODULE 2: Predicate to check if a name is valid
    private final Predicate<String> isValidName = name -> name != null && !name.isBlank();

    public GameService(GameMatchRepository repository) {
        this.repository = repository;
    }

    public GameMatchResponse createMatch(CreateMatchRequest request) {
        // MODULE 2: Using Predicate to validate names
        // MODULE 4: Throwing custom exception if invalid
        if (!isValidName.test(request.playerOneName())) {
            throw new InvalidMatchException("Player One name must not be blank.");
        }
        if (!isValidName.test(request.playerTwoName())) {
            throw new InvalidMatchException("Player Two name must not be blank.");
        }

        String playerOne = request.playerOneName().trim();
        String playerTwo = request.playerTwoName().trim();

        int playerOneScore = random.nextInt(101);
        int playerTwoScore = random.nextInt(101);
        while (playerOneScore == playerTwoScore) {
            playerTwoScore = random.nextInt(101);
        }

        String winner = playerOneScore > playerTwoScore ? playerOne : playerTwo;

        // MODULE 1: Store scores in 2D array
        int slot = scoreCount % 10;
        scoreHistory[slot][0] = playerOneScore;
        scoreHistory[slot][1] = playerTwoScore;
        scoreCount++;

        GameMatch saved = repository.save(new GameMatch(
                playerOne, playerTwo, playerOneScore, playerTwoScore, winner, request.ranked()
        ));

        return GameMatchResponse.from(saved);
    }

    // MODULE 1: Get the highest score stored in the 2D array
    public int getHighestStoredScore() {
        int max = 0;
        int limit = Math.min(scoreCount, 10);
        for (int i = 0; i < limit; i++) {
            if (scoreHistory[i][0] > max) max = scoreHistory[i][0];
            if (scoreHistory[i][1] > max) max = scoreHistory[i][1];
        }
        return max;
    }

    public int[][] getScoreHistory() {
        return scoreHistory;
    }

    public List<GameMatchResponse> listMatches() {
        // MODULE 3: Uses GameMatch.compareTo() for natural sort order
        return repository.findAll().stream()
                .sorted()
                .map(GameMatchResponse::from)
                .toList();
    }

    public GameMatchResponse getMatch(long id) {
        return repository.findById(id)
                .map(GameMatchResponse::from)
                .orElseThrow(() -> new NoSuchElementException("No match found with id " + id));
    }

    public void deleteMatch(long id) {
        repository.deleteById(id);
    }

    // MODULE 6: Groups ranked matches by winner using streams, collectors
    public List<LeaderboardEntry> getRankedLeaderboard() {
        Map<String, Long> wins = repository.findByRankedTrue().stream()
                .collect(Collectors.groupingBy(GameMatch::getWinnerName, Collectors.counting()));

        return wins.entrySet().stream()
                .map(e -> new LeaderboardEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(LeaderboardEntry::wins).reversed())
                .toList();
    }

    // MODULE 5: Recursively find the top winner from the leaderboard
    public LeaderboardEntry findTopWinner(List<LeaderboardEntry> entries, int index) {
        // Base case: last entry
        if (index == entries.size() - 1) return entries.get(index);

        // Recursive case compare current to best of the rest
        LeaderboardEntry best = findTopWinner(entries, index + 1);
        LeaderboardEntry current = entries.get(index);
        return current.wins() >= best.wins() ? current : best;
    }
}