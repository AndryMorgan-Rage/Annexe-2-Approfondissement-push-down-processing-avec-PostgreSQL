package Services;

import Connection.DBconnection;
import Model.CandidateVoteCount;
import Model.ElectionResult;
import Model.VoteSummary;
import Model.VoteTypeCount;
import Model.voteType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {


    private final Connection connection;
    public DataRetriever(Connection connection) {
        this.connection = connection;
    }

    public long countAllVotes() {
        String sql = "SELECT COUNT(id) AS total_votes FROM vote";
        long totalVotes = 0;
        Connection connection = new DBconnection().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalVotes = resultSet.getLong("total_votes");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalVotes;
    }

    public List<VoteTypeCount> countVotesByType() {
        List<VoteTypeCount> results = new ArrayList<>();
        String sql = "SELECT vote_type, COUNT(id) AS count_result FROM vote GROUP BY vote_type";
        Connection connection = new DBconnection().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String typeStr = resultSet.getString("vote_type");
                voteType typeEnum = voteType.valueOf(typeStr);

                long count = resultSet.getLong("count_result");

                results.add(new VoteTypeCount(typeEnum, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<CandidateVoteCount> countValidVotesByCandidate() {
        List<CandidateVoteCount> results = new ArrayList<>();
        Connection connection = new DBconnection().getConnection();

        String sql = "SELECT c.name AS candidate_name, " +
                "COUNT(v.id) FILTER (WHERE v.vote_type = 'VALID') AS valid_vote " +
                "FROM candidate c " +
                "LEFT JOIN vote v ON c.id = v.candidate_id " +
                "GROUP BY c.id, c.name " +
                "ORDER BY valid_vote DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("candidate_name");
                long count = resultSet.getLong("valid_vote");

                results.add(new CandidateVoteCount(name, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    public VoteSummary computeVoteSummary() {
        Connection connection = new DBconnection().getConnection();
        String sql = "SELECT " +
                "COUNT(id) FILTER (WHERE vote_type = 'VALID') AS valid_count, " +
                "COUNT(id) FILTER (WHERE vote_type = 'BLANK') AS blank_count, " +
                "COUNT(id) FILTER (WHERE vote_type = 'NULL') AS null_count " +
                "FROM vote";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new VoteSummary(
                        resultSet.getLong("valid_count"),
                        resultSet.getLong("blank_count"),
                        resultSet.getLong("null_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new VoteSummary(0, 0, 0);
    }
    public double computeTurnoutRate() {
        Connection connection = new DBconnection().getConnection();

        String sql = "SELECT ((SELECT COUNT(id) FROM vote)::double precision / " +
                "(SELECT COUNT(id) FROM voter)::double precision) * 100 AS turnout_rate";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getDouble("turnout_rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public ElectionResult findWinner() {
        Connection connection = new DBconnection().getConnection();

        String sql = "SELECT c.name AS candidate_name, COUNT(v.id) AS valid_vote_count " +
                "FROM candidate c " +
                "JOIN vote v ON c.id = v.candidate_id " +
                "WHERE v.vote_type = 'VALID' " +
                "GROUP BY c.id, c.name " +
                "ORDER BY valid_vote_count DESC " +
                "LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new ElectionResult(
                        resultSet.getString("candidate_name"),
                        resultSet.getLong("valid_vote_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}