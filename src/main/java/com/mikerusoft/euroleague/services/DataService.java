package com.mikerusoft.euroleague.services;


import com.mikerusoft.euroleague.model.Command;
import com.mikerusoft.euroleague.model.Result;
import com.mikerusoft.euroleague.model.Tournament;

import java.util.Date;
import java.util.List;

public interface DataService {

    Command insertCommand(String command);
    Tournament insertTournament (String tournament);

    Result saveResult (Result result);

    Command updateCommand(Command command);
    Tournament updateTournament (Tournament tournament);

    List<Command> getCommands();

    List<Tournament> getTournaments();

    List<Result> getLastResults(int commandId, int limit);

    List<Result> getLastResults(int tournId, int commandId, int limit);

    List<Result> getResults(int commandId, String season);
    Result getResult(int resultId);

    List<Result> getResults(int tournId, int commandId, String season);

    void deleteCommand(int cmdId);
    void deleteTournament(int tournId);
    void deleteResult(int resultId);

}
