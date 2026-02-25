package Model;

import Connection.DBconnection;
import Services.DataRetriever;

import java.sql.Connection;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Connection maConnection = new DBconnection().getConnection();
        DataRetriever retriever = new DataRetriever(maConnection);

        long count = retriever.countAllVotes();
        System.out.println("Total votes: " + count);

        List<VoteTypeCount> votesByType = retriever.countVotesByType();
        System.out.println(votesByType);

        List<CandidateVoteCount> results = retriever.countValidVotesByCandidate();
        System.out.println(results);

        double rate = retriever.computeTurnoutRate();
        System.out.println("Taux de participation = " + rate + "%");

        ElectionResult winner = retriever.findWinner();
        System.out.println(winner);
    }
}
