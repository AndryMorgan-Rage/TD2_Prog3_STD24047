package Football;

import Football.java.Player;
import Football.java.Team;
import Football.java.continentEnum;
import Football.java.positionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id) throws SQLException {
        String teamQuery = "SELECT id, name, continent FROM Team WHERE id = ?";
        String playerQuery = "SELECT id, name, age, position, goal_nb FROM Player WHERE id_team = ?";

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement teamStmt = connection.prepareStatement(teamQuery)) {

            teamStmt.setInt(1, id);
            try (ResultSet teamRs = teamStmt.executeQuery()) {
                if (!teamRs.next()) {
                    return null;
                }

                Team team = new Team(
                        teamRs.getInt("id"),
                        teamRs.getString("name"),
                        continentEnum.valueOf(teamRs.getString("continent"))
                );
                team.setPlayers(new ArrayList<>());

                try (PreparedStatement playerStmt = connection.prepareStatement(playerQuery)) {
                    playerStmt.setInt(1, id);
                    try (ResultSet playerRs = playerStmt.executeQuery()) {
                        while (playerRs.next()) {
                            Player player = new Player(
                                    playerRs.getInt("id"),
                                    playerRs.getString("name"),
                                    playerRs.getInt("age"),
                                    positionEnum.valueOf(playerRs.getString("position")),
                                    team
                            );
                            // Safe retrieval of nullable Integer
                            player.setGoalNb((Integer) playerRs.getObject("goal_nb"));
                            team.getPlayers().add(player);
                        }
                    }
                }
                return team;
            }
        }
    }


    public List<Player> findPlayers(int page, int size) throws SQLException {
        List<Player> players = new ArrayList<>();

        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("page et size doivent être > 0");
        }

        String query = """
        SELECT p.id, p.name, p.age, p.position, p.goal_nb,
       t.id AS team_id, t.name AS team_name, t.continent
            """;

        int offset = (page - 1) * size;

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement state = connection.prepareStatement(query)) {

            state.setInt(1, size);
            state.setInt(2, offset);

            ResultSet rs = state.executeQuery();

            while (rs.next()) {

                Team team = null;
                if (rs.getInt("team_id") != 0) {
                    team = new Team(
                            rs.getInt("team_id"),
                            rs.getString("team_name"),
                            continentEnum.valueOf(rs.getString("continent"))
                    );
                }

                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        positionEnum.valueOf(rs.getString("position")),
                        team
                );

                players.add(player);
            }
        }

        return players;
    }

    public List<Player> createPlayers(List<Player> newPlayers) {

        if (newPlayers == null || newPlayers.isEmpty()) {
            throw new IllegalArgumentException("liste vide");
        }

        try (Connection connection = dbConnection.getDBConnection()) {

            connection.setAutoCommit(false);

            String insertQuery = """
            INSERT INTO Player(id, name, age, position, goal_nb, id_team)
            VALUES (?, ?, ?, ?, ?, ?)
                """;

            for (Player p : newPlayers) {
                if (playerExists(connection, p.getName())) {
                    connection.rollback();
                    throw new IllegalStateException("deja existant : " + p.getName());
                }
            }

            try (PreparedStatement state = connection.prepareStatement(insertQuery)) {
                for (Player p : newPlayers) {
                    state.setInt(1, p.getId());
                    state.setString(2, p.getName());
                    state.setInt(3, p.getAge());
                    state.setObject(5, p.getGoalNb(), java.sql.Types.INTEGER);
                    if (p.getTeam() != null) {
                        state.setInt(5, p.getTeam().getId());
                    } else {
                        state.setNull(5, java.sql.Types.INTEGER);
                    }
                    if (p.getGoalNb() != null) {
                        state.setInt(5, p.getGoalNb()); // valeur connue
                    } else {
                        state.setNull(5, java.sql.Types.INTEGER); // NULL en SQL
                    }
                    state.executeUpdate();
                }
            }

            connection.commit();
            return newPlayers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean playerExists(Connection connection, String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM Player WHERE name = ?";
        try (PreparedStatement state = connection.prepareStatement(query)) {
            state.setString(1, name);
            ResultSet rs = state.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Team saveTeam(Team teamToSave) {

        if (teamToSave == null) {
            throw new IllegalArgumentException("team null");
        }

        try (Connection connection = dbConnection.getDBConnection()) {

            connection.setAutoCommit(false);

            String existsQuery = "SELECT COUNT(*) FROM Team WHERE id = ?";
            String insertQuery = "INSERT INTO Team(id, name, continent) VALUES (?, ?, ?)";
            String updateQuery = "UPDATE Team SET name = ?, continent = ? WHERE id = ?";
            String removePlayersTeam = "UPDATE Player SET id_team = NULL WHERE id_team = ?";
            String updatePlayerTeam = "UPDATE Player SET id_team = ? WHERE id = ?";

            boolean exists;

            try (PreparedStatement state = connection.prepareStatement(existsQuery)) {
                state.setInt(1, teamToSave.getId());
                ResultSet rs = state.executeQuery();
                exists = rs.next() && rs.getInt(1) > 0;
            }

            if (!exists) {
                try (PreparedStatement state = connection.prepareStatement(insertQuery)) {
                    state.setInt(1, teamToSave.getId());
                    state.setString(2, teamToSave.getName());
                    state.setString(3, teamToSave.getContinent().name());
                    state.executeUpdate();
                }
            } else {
                try (PreparedStatement state = connection.prepareStatement(updateQuery)) {
                    state.setString(1, teamToSave.getName());
                    state.setString(2, teamToSave.getContinent().name());
                    state.setInt(3, teamToSave.getId());
                    state.executeUpdate();
                }
            }

            try (PreparedStatement state = connection.prepareStatement(removePlayersTeam)) {
                state.setInt(1, teamToSave.getId());
                state.executeUpdate();
            }

            if (teamToSave.getPlayers() != null) {
                try (PreparedStatement state = connection.prepareStatement(updatePlayerTeam)) {
                    for (Player p : teamToSave.getPlayers()) {
                        state.setInt(1, teamToSave.getId());
                        state.setInt(2, p.getId());
                        state.executeUpdate();
                    }
                }
            }

            connection.commit();
            return teamToSave;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Team> findTeamsByPlayerName(String playerName) {

        List<Team> teams = new ArrayList<>();

        String query = """
 SELECT t.id AS team_id, t.name AS team_name, t.continent AS team_continent FROM Team t JOIN Player p ON p.id_team = t.id WHERE LOWER(p.name) LIKE LOWER(?) GROUP BY t.id, t.name, t.continent
        """;

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement state = connection.prepareStatement(query)) {

            state.setString(1, "%" + playerName + "%");
            ResultSet rs = state.executeQuery();

            while (rs.next()) {
                Team team = new Team(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        continentEnum.valueOf(rs.getString("team_continent"))
                );
                teams.add(team);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return teams;
    }

    public List<Player> findPlayersByCriteria(String playerName, positionEnum position, String teamName, continentEnum continent, int page, int size) {
        List<Player> players = new ArrayList<>();

        String baseQuery = """
        SELECT p.id AS player_id, p.name AS player_name, p.age AS player_age, p.position AS player_position,
               t.id AS team_id, t.name AS team_name, t.continent AS team_continent
        FROM Player p
        JOIN Team t ON p.id_team = t.id
        WHERE 1=1
        """;

        List<Object> params = new ArrayList<>();

        if (playerName != null && !playerName.isBlank()) {
            baseQuery += " AND LOWER(p.name) LIKE LOWER(?)";
            params.add("%" + playerName + "%");
        }

        if (position != null) {
            baseQuery += " AND p.position = CAST(? AS position_enum)";
            params.add(position.name());
        }
        if (teamName != null && !teamName.isBlank()) {
            baseQuery += " AND t.name ILIKE ?";
            params.add("%" + teamName + "%");
        }


        if (continent != null) {
            baseQuery += " AND t.continent = CAST(? AS continent_enum)";
            params.add(continent.name());
        }

        baseQuery += " LIMIT ? OFFSET ?";
        params.add(size);
        params.add((page - 1) * size);

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement state = connection.prepareStatement(baseQuery)) {

            for (int i = 0; i < params.size(); i++) {
                state.setObject(i + 1, params.get(i));
            }

            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                Team team = new Team(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        continentEnum.valueOf(rs.getString("team_continent"))
                );

                Player player = new Player(
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("player_age"),
                        positionEnum.valueOf(rs.getString("player_position")),
                        team
                );

                players.add(player);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }
}
