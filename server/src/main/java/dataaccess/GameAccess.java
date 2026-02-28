package dataaccess;

import model.*;

import java.util.Collection;

public interface GameAccess {

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    <GamaData> Collection<GamaData> listGame() throws DataAccessException;

    void addPlayer() throws DataAccessException;

    void clearGames() throws DataAccessException;
}
