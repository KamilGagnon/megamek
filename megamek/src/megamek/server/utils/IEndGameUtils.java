package megamek.server.utils;

import megamek.common.Game;
import megamek.common.Player;
import megamek.common.Report;
import megamek.common.enums.GamePhase;
import megamek.common.event.GameVictoryEvent;
import megamek.server.GameManager;

public interface IEndGameUtils {

    final float ELO_GAIN = 10.0f;
    static void changeToPhaseEnd(Game game, GameManager gm) {
        // remove any entities that died in the heat/end phase before
        // checking for victory
        gm.resetEntityPhase(GamePhase.END);
        boolean victory = gm.victory(); // note this may add reports
        // check phase report
        // HACK: hardcoded message ID check
        if ((gm.getVPhaseReport().size() > 3) || ((gm.getVPhaseReport().size() > 1)
                && (gm.getVPhaseReport().elementAt(1).messageId != 1205))) {
            game.addReports(gm.getVPhaseReport());
            gm.changePhase(GamePhase.END_REPORT);
        } else {
            // just the heat and end headers, so we'll add
            // the <nothing> label
            gm.addReport(new Report(1205, Report.PUBLIC));
            game.addReports(gm.getVPhaseReport());
            gm.sendReport();
            if (victory) {
                gm.changePhase(GamePhase.VICTORY);
            } else {
                gm.changePhase(GamePhase.INITIATIVE);
            }
        }
        // Decrement the ASEWAffected counter
        gm.decrementASEWTurns();
    }

    static void changeToPhaseEndReport(Game game, GameManager gm) {
        if (gm.changePlayersTeam) {
            gm.processTeamChangeRequest();
        }
        if (gm.victory()) {
            gm.changePhase(GamePhase.VICTORY);
        } else {
            gm.changePhase(GamePhase.INITIATIVE);
        }
    }
    static void changeToPhaseVictory(Game game, GameManager gm){
        GameVictoryEvent gve = new GameVictoryEvent(gm, game);
        changeEloRatingOfPlayers(game, gm);
        game.processGameEvent(gve);
        gm.transmitGameVictoryEventToAll();
        gm.resetGame();
    }

    static void changeEloRatingOfPlayers(Game game, GameManager gm){
        //get the winner
        int winner = game.getVictoryResult().getWinningPlayer();
        //turn that int into a player
        Player winnerPlayer = game.getPlayer(winner);
        //modify winnerPlayer
        winnerPlayer.modifyEloRating(ELO_GAIN);
        // get all the losers
        float eloLoss = calculateLosingEloLoss(game);
        for (Player player : game.getPlayersList()) {
            if (player != winnerPlayer) {
                player.modifyEloRating(eloLoss);
            }
        }
    }

    static float calculateLosingEloLoss(Game game){
        //get number of players
        int numPlayers = game.getPlayersList().isEmpty() ? 1 : game.getPlayersList().size();
        //calculate elo loss
        return - ELO_GAIN / (numPlayers);
    }
}
