package com.mikerusoft.euroleague.services.mysql;

import com.mikerusoft.euroleague.model.*;
import com.mikerusoft.euroleague.modelToEntityConvertor.Converter;
import com.mikerusoft.euroleague.repositories.mysql.CommandRepository;
import com.mikerusoft.euroleague.repositories.mysql.MatchStatRepository;
import com.mikerusoft.euroleague.repositories.mysql.ResultRepository;
import com.mikerusoft.euroleague.repositories.mysql.TournamentRepository;
import com.mikerusoft.euroleague.services.DataService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataServiceJdbc implements DataService<Integer> {

    private CommandRepository commandRepository;
    private TournamentRepository tournamentRepository;
    private ResultRepository resultRepository;
    private MatchStatRepository matchStatRepository;

    public DataServiceJdbc(CommandRepository commandRepository, TournamentRepository tournamentRepository,
                           ResultRepository resultRepository, MatchStatRepository matchStatRepository) {
        this.commandRepository = commandRepository;
        this.tournamentRepository = tournamentRepository;
        this.resultRepository = resultRepository;
        this.matchStatRepository = matchStatRepository;
    }

    @Override
    public Command getCommand(Integer commandId) {
        return Converter.convert(commandRepository.getOne(commandId));
    }

    @Override
    public Command insertCommand(String command) {
        return Converter.convert(
            commandRepository.save(com.mikerusoft.euroleague.entities.mysql.Command.builder().commandName(command).build())
        );
    }

    @Override
    public Tournament insertTournament(String tournament) {
        return Converter.convert(
            tournamentRepository.save(com.mikerusoft.euroleague.entities.mysql.Tournament.builder().tournName(tournament).build())
        );
    }

    @Override
    public Tournament getTournament(Integer tournId) {
        return Converter.convert(tournamentRepository.getOne(tournId));
    }

    @Override
    public Result saveResult(Result result) {
        return Converter.convert(
            resultRepository.save(Converter.convert(result))
        );
    }

    @Override
    public Command updateCommand(Command command) {
        return Converter.convert(
            commandRepository.save(Converter.convert(command))
        );
    }

    @Override
    public Tournament updateTournament(Tournament tournament) {
        return Converter.convert(
            tournamentRepository.save(Converter.convert(tournament))
        );
    }

    @Override
    public List<Command> getCommands() {
        return commandRepository.findAll().stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Tournament> getTournaments() {
        return tournamentRepository.findAll().stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Result> getLastResults(Integer commandId, int limit) {
        return resultRepository.findResultsByCommandId(commandId, PageRequest.of(0, limit))
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Result> getLastResults(Integer tournId, Integer commandId, int limit) {
        return resultRepository.findResultsByCommandIdAndTournId(tournId, commandId, PageRequest.of(0, limit))
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Result> getResults(Integer commandId, String season) {
        return resultRepository.findResultsByCommandIdAndSeason(commandId, season)
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public Result getResult(Integer resultId) {
        return Converter.convert(resultRepository.getOne(resultId));
    }

    @Override
    public List<Result> getResults(Integer tournId, Integer commandId, String season) {
        return resultRepository.findResultsByCommandIdAndTournIdAndSeason(tournId, commandId, season)
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommand(Integer cmdId) {
        resultRepository.deleteByCommandId(cmdId);
        commandRepository.deleteById(cmdId);
    }

    @Override
    @Transactional
    public void deleteTournament(Integer tournId) {
        resultRepository.deleteByTournamentId(tournId);
        tournamentRepository.deleteById(tournId);
    }

    @Override
    @Transactional
    public void deleteResult(Integer resultId) {
        resultRepository.deleteById(resultId);
    }

    @Override
    public void deleteMatch(Integer matchId) {

    }

    @Override
    public void deleteCommandMatchStat(Integer matchId, Integer commandId) {

    }

    @Override
    public void deleteQuarterStat(Integer matchId, Integer commandId, Quarter quarter, Quarter... quarters) {

    }

    @Override
    public CommandMatchStat createMatchStat(CommandMatchStat matchStat) {
        com.mikerusoft.euroleague.entities.mysql.CommandMatchStat commandMatchStat = Converter.convert(matchStat);
        return Converter.convert(matchStatRepository.saveAndFlush(commandMatchStat));
    }

    @Override
    public CommandMatchStat updateMatchStat(CommandMatchStat matchStat) {
        com.mikerusoft.euroleague.entities.mysql.CommandMatchStat commandMatchStat = Converter.convert(matchStat);
        return Converter.convert(matchStatRepository.saveAndFlush(commandMatchStat));
    }

    @Override
    public void writeAllResults(OutputStream outputStream) throws IOException {
        List<com.mikerusoft.euroleague.entities.mysql.Result> all = resultRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(outputStream));
        bfw.write("TournamentId, TournamentName, CommandId, CommandName, ResultId, Season, MatchDate, IsHome, ScoredIn, ScoredOut," +
                "Scored1, Attempts1, Scored2, Attempts2, Scored3, Attempts3");
        bfw.newLine();

        for (com.mikerusoft.euroleague.entities.mysql.Result r : all) {
            bfw.write(r.getTournament().getId() + "," + wrap(r.getTournament().getTournName()) + "," +
                    r.getCommand().getId() + "," + wrap(r.getCommand().getCommandName()) + "," +
                    r.getId() + "," + wrap(r.getSeason()) + "," + wrap((r.getDate().toLocalDate().format(formatter))) + "," +
                    r.isHomeMatch() + "," + r.getScoreIn() + "," + r.getScoreOut() + "," +
                    r.getScored1Points() + "," + r.getAttempts1Points() + "," +
                    r.getScored2Points() + "," + r.getAttempts2Points() + "," +
                    r.getScored3Points() + "," + r.getAttempts3Points()
            );
            bfw.newLine();
            bfw.flush();
        }
        bfw.flush();
        bfw.close();
    }

    private static String wrap(String str) {
        return "`" + str + "`";
    }
}
