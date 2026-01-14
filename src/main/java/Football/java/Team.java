package Football.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    private int id;
    private String name;
    private continentEnum continent;
    private List<Player> players = new ArrayList<Player>();

    public Team(int id, String name, continentEnum continent) {
        this.id = id;
        this.name = name;
        this.continent = continent;
    }



    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", continent=" + continent +
                '}';
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public continentEnum getContinent() {
        return continent;
    }

    public void setContinent(continentEnum continent) {
        continent = continent;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getPlayersGoals() {
        int totalGoals = 0;
        for (Player player : players) {
            Integer goals = player.getGoalNb();
            if (goals == null) {
                throw new IllegalStateException("Nombre de buts inconnu pour le joueur : " + player.getName());
            }
            totalGoals += goals;
        }
        return totalGoals;
    }



}
