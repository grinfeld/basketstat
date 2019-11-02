package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.controllers.model.Action;
import com.mikerusoft.euroleague.controllers.model.ResultAvg;
import com.mikerusoft.euroleague.menus.MenuProperties;
import com.mikerusoft.euroleague.model.*;
import com.mikerusoft.euroleague.model.exceptions.AssertResultException;
import com.mikerusoft.euroleague.services.DataService;
import com.mikerusoft.euroleague.utils.Validations;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.*;

import static com.mikerusoft.euroleague.model.Quarter.OT;
import static com.mikerusoft.euroleague.utils.Utils.*;

@Controller
@Slf4j
public class BasketStatController {

    private int matchesMaxNumber;
    private List<MenuProperties.Menu> menus;
    private DataService<Integer> dataService;
    private DataService<String> dataServiceMongo;

    @Autowired
    public BasketStatController(@Value("${matches-max-number}") int matchesMaxNumber,
                                MenuProperties menu, DataService<Integer> dataService, DataService<String> dataServiceMongo) {
        this.menus = menu.getMenus();
        this.dataService = dataService;
        this.dataServiceMongo = dataServiceMongo;
        this.matchesMaxNumber = matchesMaxNumber;
    }

    @GetMapping({"/", "/index.html", "/home.html"})
    public String main(Model model) {
        fillModelWithInitialData(model);
        return "index";
    }

    @ExceptionHandler({AssertResultException.class})
    public String handleDemoException(AssertResultException exception,
                                      HttpServletRequest req) {
        req.setAttribute("javax.servlet.error.status_code",
                HttpStatus.BAD_REQUEST.value());
        req.setAttribute("error", exception.getMessage());
        req.setAttribute("menus", this.menus);

        fillCommand(commands -> req.setAttribute("commands", commands));
        fillTournament(tournaments -> req.setAttribute("tournaments", tournaments));
        return "".equals(exception.getFromPage()) ? "error" : exception.getFromPage();
    }

    @GetMapping({"/compare.html"})
    public String compareGet(@RequestParam("tournamentId") Optional<String> tournamentId,
                             @ModelAttribute("filter") Optional<CompareFilter> filter,
                             Model model) {
        return compare(tournamentId, filter, model);
    }

    @PostMapping({"/compare.html"})
    public String comparePost(@RequestParam("tournamentId") Optional<String> tournamentId,
                             @ModelAttribute("filter") Optional<CompareFilter> filter,
                             Model model) {
        return compare(tournamentId, filter, model);
    }

    private String compare(Optional<String> tournamentId,
                             Optional<CompareFilter> filter,
                             Model model) {
        fillModelWithInitialData(model);

        CompareFilter compareFilter = fillFilter(tournamentId, filter, model);

        model.addAttribute("filter", compareFilter);

        if (compareFilter.filterReady()) {
            List<Match> command1Stat = dataServiceMongo.findByCommandInTournamentAndSeason(compareFilter.getTournamentId(),
                    compareFilter.getSeason(), compareFilter.getCommand1Id(), Place.byName(compareFilter.getMatchPlace()), compareFilter.getRecords());
            List<Match> command2Stat = dataServiceMongo.findByCommandInTournamentAndSeason(compareFilter.getTournamentId(),
                    compareFilter.getSeason(), compareFilter.getCommand2Id(), Place.byName(compareFilter.getMatchPlace()), compareFilter.getRecords());
            model.addAttribute("command1Stats", command1Stat);
            model.addAttribute("command2Stats", command2Stat);

            model.addAttribute("command1Aggr", makeAggregation(command1Stat, compareFilter.getCommand1Id()));
            model.addAttribute("command2Aggr", makeAggregation(command2Stat, compareFilter.getCommand2Id()));
        }

        return "compare.html";
    }

    private Aggr makeAggregation(List<Match> commandStat, String commandId) {
        Aggr collect = commandStat.stream().filter(m -> isCommandInMatch(commandId, m))
                .collect(new StatCollector(commandId));
        return collect;
    }

    private static boolean isCommandInMatch(String commandId, Match m) {
        return m.getHomeCommand().getCommand().getId().equals(commandId) || m.getAwayCommand().getCommand().getId().equals(commandId);
    }

    private CompareFilter fillFilter(Optional<String> tournamentId, Optional<CompareFilter> filter, Model model) {
        Date now = new Date(System.currentTimeMillis());

        CompareFilter compareFilter = filter.orElseGet(() -> CompareFilter.builder().matchPlace(Place.all.name()).season(extractSeason(now)).build());

        fillFilterField(tournamentId, t -> !isEmptyTrimmed(t), this::getTournament, Objects::nonNull,
            tournament -> {
                compareFilter.setTournament(tournament);
                model.addAttribute("tournament", tournament);
            }
        );
        return compareFilter;
    }

    private static <T, R> void fillFilterField(T obj, Predicate<T> enterPredicate, Function<T, R> action,
                                               Predicate<R> resultPredicate, Consumer<R> resultPerform) {
        Optional.ofNullable(obj).filter(enterPredicate).map(action).filter(resultPredicate).ifPresent(resultPerform);
    }

    @GetMapping(value = {"/creatematch", "creatematch.html"})
    public String createMatch(@RequestParam("tournament") Optional<String> tournament, Model model) {
        return editMatch(null, tournament.orElse(null), null, model);
    }

    @GetMapping("/editmatch")
    public String editMatchGet(@RequestParam("matchId") Optional<String> matchId, Model model) {
        return editMatch(null, null, matchId.orElse(null), model);
    }

    @PostMapping("/editmatch")
    public String editMatch(@RequestParam("matchId") Optional<String> matchId, @ModelAttribute("match") Optional<Match> match, Model model) {
        return editMatch(match.orElse(null), null, matchId.orElse(null), model);
    }

    private String editMatch(Match match, String tournamentId, String matchId, Model model) {
        try {
            fillModelWithInitialData(model);

            if (match == null && !isEmptyTrimmed(matchId)) {
                match = dataServiceMongo.getMatch(matchId);
            }

            if (match == null) {
                Date now = new Date(System.currentTimeMillis());
                match = Match.builder().tournament(Tournament.builder().id(isEmptyTrimmed(tournamentId) ? null : tournamentId.trim()).build())
                        .date(now)
                        .season(extractSeason(now))
                        .awayCommand(CommandMatchStat.builder().command(Command.builder().build()).quarterStats(CommandQuarterStat.initialCommandStats()).build())
                        .homeCommand(CommandMatchStat.builder().command(Command.builder().build()).quarterStats(CommandQuarterStat.initialCommandStats()).build())
                    .build();
            }

            match = Match.normalizeMatch(match);

            model.addAttribute("currentMatch", match);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("", e);
        }
        return "editmatch";
    }

    @PostMapping("/savematch")
    public String saveMatch(@ModelAttribute("currentMatch") Match match, Model model) {
        try {
            Validations.validateMatch(match);
            match.setHasOvertime(hasOvertime(match));

            Match updatedMatch = Optional.ofNullable(dataServiceMongo.saveMatch(match)).map(Match::normalizeMatch)
                        .orElseThrow(() -> new IllegalArgumentException("for unknown reason, something went wrong"));
            assertNotNull(updatedMatch, "Something went wrong");
            model.addAttribute("currentMatch", updatedMatch);
            model.addAttribute("msg", "Match saved successfully");
        } catch (Exception e) {
            model.addAttribute("currentMatch", match);
            model.addAttribute("error", e.getMessage());
            log.error("", e);
        }
        return "forward:/editmatch";
    }

    private boolean hasOvertime(@ModelAttribute("currentMatch") Match match) {
        int scoreHome = match.getHomeCommand().getQuarterStats().stream().filter(t -> OT.is(t.getQuarter()))
                .mapToInt(CommandQuarterStat::getScore).findAny().orElse(0);
        int scoreAway = match.getAwayCommand().getQuarterStats().stream().filter(t -> OT.is(t.getQuarter()))
                .mapToInt(CommandQuarterStat::getScore).findAny().orElse(0);
        return scoreHome > 0 || scoreAway > 0;
    }

    @PostMapping("/view")
    public String main(@ModelAttribute("command") String command, @ModelAttribute("tournament") String tournament,
                       @ModelAttribute("result") String result, @ModelAttribute("action") String action, Model model) {

        fillModelWithInitialData(model);

        Integer commandId = convertString(command);
        Integer tournId = convertString(tournament);

        if (!fillModelWithCommandAndTournament(model, commandId, tournId)) return "index";

        Action actionEnum = Action.byName(action);
        Integer resultId = convertString(result);

        if ( (resultId == null && actionEnum != Action.create) || needLastResultsAfterHandleAction(model, commandId, tournId, actionEnum, resultId)) {
            fillLastResults(model, commandId, tournId);
        }

        return "index";
    }

    private void fillModelWithInitialData(Model model) {
        model.addAttribute("menus", this.menus);
        if (model.getAttribute("setResult") == null)
            model.addAttribute("setResult", false);
        fillCommand(commands -> model.addAttribute("commands", commands));
        fillTournament(tournaments -> model.addAttribute("tournaments", tournaments));
    }

    private void fillTournament(Consumer<List<Tournament>> consumer) {
        List<Tournament> tournaments = dataServiceMongo.getTournaments();
        tournaments.sort((o1, o2) -> o1.getTournName().compareToIgnoreCase(o2.getTournName()));
        consumer.accept(tournaments);
    }

    private void fillCommand(Consumer<List<Command>> consumer) {
        List<Command> commands = Optional.ofNullable(dataServiceMongo.getCommands()).orElseGet(ArrayList::new);
        commands.sort((o1, o2) -> o1.getCommandName().compareToIgnoreCase(o2.getCommandName()));
        consumer.accept(commands);
    }

    @GetMapping({"/command.html"})
    public String command(Model model) {
        model.addAttribute("menus", this.menus);
        model.addAttribute("command", Command.builder().build());
        fillCommand(commands -> model.addAttribute("commands", commands));
        return "command";
    }

    @PostMapping({"/commandsave", "/commandsave/"})
    public String editCommand(@ModelAttribute("command") Command command, Model model) {
        model.addAttribute("menus", this.menus);

        command = isEmptyTrimmed(command.getId()) ? dataServiceMongo.insertCommand(command.getCommandName()) :
                dataServiceMongo.updateCommand(command);
        model.addAttribute("command", command);
        fillCommand(commands -> model.addAttribute("commands", commands));
        return "command";
    }

    @PostMapping({"/tournamentsave", "/tournamentsave/"})
    public String editTournament(@ModelAttribute("tourn") Tournament tournament, Model model) {
        model.addAttribute("menus", this.menus);

        tournament = isEmptyTrimmed(tournament.getId()) ? dataServiceMongo.insertTournament(tournament.getTournName()) :
                dataServiceMongo.updateTournament(tournament);
        model.addAttribute("tourn", tournament);
        fillTournament(tournaments -> model.addAttribute("tournaments", tournaments));
        return "tournament";
    }

    @GetMapping({"/tournament.html"})
    public String tournament(Model model) {
        model.addAttribute("menus", this.menus);
        model.addAttribute("tourn", Tournament.builder().build());
        fillTournament(tournaments -> model.addAttribute("tournaments", tournaments));
        return "tournament";
    }

    private Tournament getTournament(Optional<String> tournament) {
        if (!tournament.isPresent())
            throw new NullPointerException("No tournament");
        return dataServiceMongo.getTournament(tournament.get());
    }

    private Command getCommand(Optional<String> command) {
        if (!command.isPresent())
            throw new NullPointerException("No command");
        return dataServiceMongo.getCommand(command.get());
    }

    @PostMapping({"/edit"})
    public String edit(@ModelAttribute("result") Result result, Model model) {
        fillModelWithInitialData(model);

        Integer commandId = result.getCommand() != null ? parseIntWithEmptyToNull(result.getCommand().getId()) : null;
        Integer tournId = result.getTournament() != null ? parseIntWithEmptyToNull(result.getTournament().getId()) : null;

        if (!fillModelWithCommandAndTournament(model, commandId, tournId)) return "index";
        try {
            Result.assertResult(result);
        } catch (Exception e) {
            throw new AssertResultException(e.getMessage(), "index");
        }

        result = Result.normalizeData(result);

        Result updatedResult = dataService.saveResult(result);

        model.addAttribute("currentResult", updatedResult);
        model.addAttribute("setResult", true);

        model.addAttribute("msg", "Successfully updated result");

        return "index";
    }

    private boolean needLastResultsAfterHandleAction(Model model, Integer commandId, Integer tournId, Action actionEnum, Integer resultId) {
        boolean needLastResults = true;
        switch (actionEnum) {
            case delete:
                dataService.deleteResult(resultId);
                break;
            case edit:
                Result foundResult = dataService.getResult(resultId);
                model.addAttribute("currentResult", foundResult);
                model.addAttribute("setResult", true);
                needLastResults = false;
                break;
            case create:
                model.addAttribute("setResult", true);
                Date now = new Date(System.currentTimeMillis());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                year = month > 1 && month <= 6 ? year - 1 : year;
                calendar.set(Calendar.YEAR, year + 1);
                model.addAttribute("currentResult", Result.builder()
                        .date(now)
                        .season(year + String.valueOf(calendar.get(Calendar.YEAR)))
                        .command(dataService.getCommand(commandId))
                        .tournament(deNull(tournId, id -> dataService.getTournament(id)))
                        .build()
                );
                needLastResults = false;
                break;
        }

        return needLastResults;
    }

    private static boolean fillModelWithCommandAndTournament(Model model, Integer commandId, Integer tournId) {
        if (commandId == null) {
            model.addAttribute("error", "You should choose at least command");
            return false;
        }

        model.addAttribute("commandId", commandId);
        model.addAttribute("tournamentId", tournId);
        return true;
    }

    private void fillLastResults(Model model, Integer commandId, Integer tournId) {
        List<Result> lastResults = tournId == null ?
                dataService.getLastResults(commandId, matchesMaxNumber) :
                dataService.getLastResults(tournId, commandId, matchesMaxNumber);

        ResultAvg avg = ResultAvg.aggregateResults(lastResults);

        model.addAttribute("results1st", lastResults);
        model.addAttribute("avg1st", avg);
    }

}