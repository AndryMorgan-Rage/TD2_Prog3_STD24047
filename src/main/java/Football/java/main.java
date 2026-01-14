package Football.java;

import Football.DataRetriever;

import java.sql.SQLException;

public class main {

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        try {
            Team team = dataRetriever.findTeamById(1);

            if (team == null) {
                System.out.println("Aucune équipe trouvée pour l'ID donné.");
                return;
            }

            System.out.println("Equipe : " + team);

            for (Player p : team.getPlayers()) {
                System.out.println(p.getName() + " - buts : " + p.getGoalNb());
            }

            System.out.println("Total buts : " + team.getPlayersGoals());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}