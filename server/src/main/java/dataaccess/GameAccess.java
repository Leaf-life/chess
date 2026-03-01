package dataaccess;

import model.*;

import java.util.Collection;

public interface GameAccess {

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void deleteGame(GameData game);

    <GamaData> Collection<GamaData> listGame() throws DataAccessException;

    void clearGames();
}
