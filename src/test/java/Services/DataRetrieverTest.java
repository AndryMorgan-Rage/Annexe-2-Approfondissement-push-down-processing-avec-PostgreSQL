
package Services;

import Connection.DBconnection;
import Model.ElectionResult;
import Model.VoteTypeCount;
import Model.voteType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataRetrieverTest {

    private DataRetriever retriever;

    @BeforeEach
    void setUp() {
        Connection connection = new DBconnection().getConnection();
        retriever = new DataRetriever(connection);
    }

    @Test
    void testCountAllVotes() {
        long count = retriever.countAllVotes();
        assertEquals(6, count, "Le nombre total de votes doit être 6");
    }

    @Test
    void testCountVotesByType() {
        List<VoteTypeCount> results = retriever.countVotesByType();
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
        boolean foundValid = results.stream()
                .anyMatch(v -> v.getVoteType().equals(voteType.VALID) && v.getCount() == 3);
        Assertions.assertTrue(foundValid, "On devrait trouver 3 votes VALID");
    }

    @Test
    void testComputeTurnoutRate() {
        double rate = retriever.computeTurnoutRate();
        assertEquals(100.0, rate, 0.01);
    }

    @Test
    void testFindWinner() {
        ElectionResult winner = retriever.findWinner();
        Assertions.assertNotNull(winner);
        assertEquals("Alice", winner.getCandidateName());
        assertEquals(2, winner.getValidVoteCount());
    }
}