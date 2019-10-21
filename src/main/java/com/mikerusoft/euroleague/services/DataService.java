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

    Result saveResult (Result result);

    Command updateCommand(Command command);
    Tournament updateTournament (Tournament tournament);

    List<Command> getCommands();

    List<Tournament> getTournaments();

    List<Result> getLastResults(T commandId, int limit);

    List<Result> getLastResults(T tournId, T commandId, int limit);

    void writeAllResults(OutputStream outputStream) throws IOException;

    List<Result> getResults(T commandId, String season);

    List<Result> getResults(T tournId, T commandId, String season);

    Result getResult(T resultId);

    void deleteCommand(T cmdId);
    void deleteTournament(T tournId);
    void deleteResult(T resultId);
}
