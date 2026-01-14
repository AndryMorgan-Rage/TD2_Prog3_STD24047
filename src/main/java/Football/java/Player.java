package Football.java;

public class Player {
    private int id;
    private String name;
    private int age;
    private positionEnum position;
    private Team team;
    private Integer goalNb;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", position=" + position +
                ", teamName=" + (team != null ? team.getName() : "Libre") +
                '}';
    }

    public Player(int id, String name, int age, positionEnum position, Team team) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.goalNb = goalNb;
        this.team = team;
    }

    public Integer getGoalNb() {
        return goalNb;
    }

    public void setGoalNb(Integer goalNb) {
        this.goalNb = goalNb;
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

    public int getAge() {
        return age;
    }
}
