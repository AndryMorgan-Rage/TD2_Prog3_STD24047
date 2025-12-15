package Football;

import java.util.List;

public class Team {
    private int id;
    private String name;
    private continentEnum Continent;
    private List<Player> players;

    public Team(int id, String name, continentEnum ContinentEnum) {
        this.id = id;
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
        return Continent;
    }

    public void setContinent(continentEnum continent) {
        Continent = continent;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }



}
