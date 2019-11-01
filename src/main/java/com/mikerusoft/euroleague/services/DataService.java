package com.mikerusoft.euroleague.services;

import com.mikerusoft.euroleague.model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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

    default List<Result> getLastResults(T commandId, int limit){ return new ArrayList<>(0); }
    default List<Result> getLastResults(T tournId, T commandId, int limit){ return new ArrayList<>(0); }
    default void writeAllResults(OutputStream outputStream) throws IOException {}
    default List<Result> getResults(T commandId, String season){ return new ArrayList<>(0); }
    default List<Result> getResults(T tournId, T commandId, String season){ return new ArrayList<>(0); }
    default Result getResult(T resultId){ return null; }
    default void deleteResult(T resultId) {}
    default Result saveResult (Result result){ return null; }

    default com.mikerusoft.euroleague.model.Match saveMatch(com.mikerusoft.euroleague.model.Match match) { return null; }
    default com.mikerusoft.euroleague.model.Match getMatch(String matchId) { return null; }
    default List<com.mikerusoft.euroleague.model.Match> findByCommandsInTournamentAndSeason(String tournId, String season, String homeCommandId, String awayCommandId, int records) { return new ArrayList<>(0); }
    default List<com.mikerusoft.euroleague.model.Match> findByCommandInTournamentAndSeason(String tournId, String season, String commandId, int records) { return new ArrayList<>(0); }

}
