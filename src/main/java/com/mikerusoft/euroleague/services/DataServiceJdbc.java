package com.mikerusoft.euroleague.services;

import com.mikerusoft.euroleague.model.Command;
import com.mikerusoft.euroleague.model.Result;
import com.mikerusoft.euroleague.model.Tournament;
import com.mikerusoft.euroleague.modelToEntityConvertor.Converter;
import com.mikerusoft.euroleague.repositories.CommandRepository;
import com.mikerusoft.euroleague.repositories.ResultRepository;
import com.mikerusoft.euroleague.repositories.TournamentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataServiceJdbc implements DataService {

    private CommandRepository commandRepository;
    private TournamentRepository tournamentRepository;
    private ResultRepository resultRepository;

    public DataServiceJdbc(CommandRepository commandRepository, TournamentRepository tournamentRepository, ResultRepository resultRepository) {
        this.commandRepository = commandRepository;
        this.tournamentRepository = tournamentRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public Command getCommand(int commandId) {
        return Converter.convert(commandRepository.getOne(commandId));
    }

    @Override
    public Command insertCommand(String command) {
        return Converter.convert(
            commandRepository.save(com.mikerusoft.euroleague.entities.Command.builder().commandName(command).build())
        );
    }

    @Override
    public Tournament insertTournament(String tournament) {
        return Converter.convert(
            tournamentRepository.save(com.mikerusoft.euroleague.entities.Tournament.builder().tournName(tournament).build())
        );
    }

    @Override
    public Tournament getTournament(int tournId) {
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
    public List<Result> getLastResults(int commandId, int limit) {
        return resultRepository.findResultsByCommandId(commandId, PageRequest.of(0, limit))
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Result> getLastResults(int tournId, int commandId, int limit) {
        return resultRepository.findResultsByCommandIdAndTournId(tournId, commandId, PageRequest.of(0, limit))
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Result> getResults(int commandId, String season) {
        return resultRepository.findResultsByCommandIdAndSeason(commandId, season)
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    public Result getResult(int resultId) {
        return Converter.convert(resultRepository.getOne(resultId));
    }

    @Override
    public List<Result> getResults(int tournId, int commandId, String season) {
        return resultRepository.findResultsByCommandIdAndTournIdAndSeason(tournId, commandId, season)
                .stream().map(Converter::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommand(int cmdId) {
        resultRepository.deleteByCommandId(cmdId);
        commandRepository.deleteById(cmdId);
    }

    @Override
    @Transactional
    public void deleteTournament(int tournId) {
        resultRepository.deleteByTournamentId(tournId);
        tournamentRepository.deleteById(tournId);
    }

    @Override
    @Transactional
    public void deleteResult(int resultId) {
        resultRepository.deleteById(resultId);
    }

    @Override
    public void writeAllResults(OutputStream outputStream) throws IOException {
        List<com.mikerusoft.euroleague.entities.Result> all = resultRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(outputStream));
        bfw.write("TournamentId, TournamentName, CommandId, CommandName, ResultId, Season, MatchDate, IsHome, ScoredIn, ScoredOut," +
                "Scored1, Attempts1, Scored2, Attempts2, Scored3, Attempts3");
        bfw.newLine();

        for (com.mikerusoft.euroleague.entities.Result r : all) {
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
