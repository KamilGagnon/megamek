package megamek.server.victory;

import megamek.common.Game;
import megamek.common.Report;
import megamek.common.enums.GamePhase;
import megamek.common.force.Forces;
import megamek.common.options.GameOptions;
import megamek.server.GameManager;
import megamek.server.utils.IEndGameUtils;
import megamek.server.victory.VictoryResult;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class EndGameUtilsTest {

    protected Game createMockedGame() {
        Game testGame = mock(Game.class);
        Forces testForces = new Forces(testGame);
        when(testGame.getGameListeners()).thenReturn(new Vector<>());
        when(testGame.getEntities()).thenReturn(Collections.emptyIterator());
        when(testGame.getEntitiesVector()).thenReturn(Collections.emptyList());
        when(testGame.getPlayers()).thenReturn(Collections.emptyEnumeration());
        when(testGame.getPlayersList()).thenReturn(Collections.emptyList());
        when(testGame.getAttacks()).thenReturn(Collections.emptyEnumeration());
        when(testGame.getAttacksVector()).thenReturn(new Vector<>());
        when(testGame.getForces()).thenReturn(testForces);
        when(testGame.getOptions()).thenReturn(new GameOptions());
        when(testGame.getOutOfGameEntitiesVector()).thenReturn(new Vector<>());
        return testGame;
    }

    @Test
    public void testChangeToPhaseEnd() {
        Game game = createMockedGame();
        GameManager gm = mock(GameManager.class);
        when(gm.victory()).thenReturn(false);
        when(gm.getvPhaseReport()).thenReturn(new Vector<>());
        when(gm.getVPhaseReport()).thenReturn(new Vector<>());

        IEndGameUtils.changeToPhaseEnd(game, gm);

        // Check if the phase has been changed to INITIATIVE
        verify(gm, times(1)).changePhase(GamePhase.INITIATIVE);
    }

    @Test
    public void testChangeToPhaseEndReport() {
        Game game = createMockedGame();
        GameManager gm = mock(GameManager.class);
        when(gm.victory()).thenReturn(false);
        when(gm.getVPhaseReport()).thenReturn(new Vector<>());

        IEndGameUtils.changeToPhaseEndReport(game, gm);

        // Check if the phase has been changed to INITIATIVE
        verify(gm, times(1)).changePhase(GamePhase.INITIATIVE);
    }

    @Test
    public void testChangeToPhaseVictory() {
        Game game = createMockedGame();
        GameManager gm = mock(GameManager.class);
        when(gm.getVPhaseReport()).thenReturn(new Vector<>());

        IEndGameUtils.changeToPhaseVictory(game, gm);

        // Check if game reset method is called
        verify(gm, times(1)).resetGame();
    }
}
