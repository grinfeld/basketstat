package com.mikerusoft.euroleague.services;

import com.mikerusoft.euroleague.model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface DataService<T> {

    Command insertCommand(String command);
    Command getCommand(T commandId);
    Tournament insertTournament (String tournament);
    Tournament getTournament(T tournId);
    Command updateCommand(Command command);
    Tournament updateTournament (Tournament tournament);
    List<Command> getCommands();
    List<Tournament> getTournaments();
    void deleteCommand(T cmdId);
    void deleteTournament(T tournId);

    default List<Result> getLastResults(T commandId, int limit){ return null; }
    default List<Result> getLastResults(T tournId, T commandId, int limit){ return null; }
    default void writeAllResults(OutputStream outputStream) throws IOException {}
    default List<Result> getResults(T commandId, String season){ return null; }
    default List<Result> getResults(T tournId, T commandId, String season){ return null; }
    default Result getResult(T resultId){ return null; }
    default void deleteResult(T resultId) {}
    default Result saveResult (Result result){ return null; }

    default public com.mikerusoft.euroleague.model.Match createMatch(com.mikerusoft.euroleague.model.Match match) { return null; }
    default public com.mikerusoft.euroleague.model.Match getMatch(String matchId) { return null; }
}
