package Football;

public class Player {
    private int id;
    private String name;
    private positionEnum position;
    private Team team;

    public Player(int id, String name, positionEnum position, Team team) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.team = team;
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

    public positionEnum getPosition() {
        return position;
    }

    public void setPosition(positionEnum position) {
        this.position = position;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
