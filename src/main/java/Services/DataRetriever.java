package Services;

import Connection.DBconnection;
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
}
