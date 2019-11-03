package com.mikerusoft.euroleague.services;

import com.mikerusoft.euroleague.model.*;

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

    Match saveMatch(com.mikerusoft.euroleague.model.Match match);
    Match getMatch(String matchId);
    List<Match> findByCommandsInTournamentAndSeason(String tournId, String season, String homeCommandId, String awayCommandId, int records);
    List<Match> findByCommandInTournamentAndSeason(String tournId, String season, String commandId, Place place, int records);

}
