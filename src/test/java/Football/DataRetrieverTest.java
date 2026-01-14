package Football;

import static org.junit.jupiter.api.Assertions.*;


import Football.java.Player;
import Football.java.Team;
import Football.java.continentEnum;
import Football.java.positionEnum;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataRetrieverTest {

    private DataRetriever retriever;

    @BeforeAll
    void setup() {
        retriever = new DataRetriever();
        // Ici, tu devrais idéalement vider tes tables de test ou
        // pointer vers une base de données H2 (mémoire) pour ne pas casser la prod.
    }

    @Test
    @DisplayName("Test de récupération d'une équipe par son ID")
    void testFindTeamById() throws SQLException {
        // On suppose que l'ID 1 existe en base (ex: Real Madrid)
        Team team = retriever.findTeamById(1);

        if (team != null) {
            assertNotNull(team.getName());
            System.out.println("Équipe trouvée : " + team.getName());
        } else {
            assertNull(team, "L'équipe n'existe pas en base");
        }
    }

    @Test
    @DisplayName("Test de la pagination des joueurs")
    void testFindPlayersPagination() throws SQLException {
        // Test avec page 1, taille 5
        List<Player> players = retriever.findPlayers(1, 5);

        assertNotNull(players);
        assertTrue(players.size() <= 5);
    }

    @Test
    @DisplayName("Test de l'exception si page ou size est invalide")
    void testFindPlayersInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            retriever.findPlayers(0, 5);
        });
    }


    @Test
    @DisplayName("Test de la transaction rollback (doublon)")
    void testCreatePlayersRollback() throws SQLException {
        // 1. Récupère d'abord une équipe valide qui existe en base (ex: ID 1)
        Team existingTeam = retriever.findTeamById(1);

        // 2. Crée un joueur avec cette équipe valide pour passer la contrainte NOT NULL
        Player p1 = new Player(999, "Test Player", 25, positionEnum.STR, existingTeam);

        // 3. On crée la liste avec le doublon
        List<Player> listWithDuplicate = List.of(p1, p1);

        // Maintenant, le test devrait bien lancer l'IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            retriever.createPlayers(listWithDuplicate);
        });
    }

    @Test
    @DisplayName("Test de recherche complexe par critères")
    void testFindPlayersByCriteria() {
        // Recherche de tous les attaquants (FW) en Europe
        List<Player> results = retriever.findPlayersByCriteria(
                null,
                positionEnum.STR,
                null,
                continentEnum.EUROPA,
                1, 10
        );

        assertNotNull(results);
        for (Player p : results) {
            assertEquals(positionEnum.STR, p.getPosition());
            assertEquals(continentEnum.EUROPA, p.getTeam().getContinent());
        }
    }
}