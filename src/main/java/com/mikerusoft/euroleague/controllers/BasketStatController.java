package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.controllers.model.Aggr;
import com.mikerusoft.euroleague.menus.MenuProperties;
import com.mikerusoft.euroleague.model.*;
import com.mikerusoft.euroleague.services.DataService;
import com.mikerusoft.euroleague.utils.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private List<MenuProperties.Menu> menus;
    private DataService<String> dataServiceMongo;

    @Autowired
    public BasketStatController(MenuProperties menu, DataService<String> dataServiceMongo) {
        this.menus = menu.getMenus();
        this.dataServiceMongo = dataServiceMongo;
    }

    @ExceptionHandler
    public String handleDemoException(Exception exception,
                                      HttpServletRequest req) {
        req.setAttribute("javax.servlet.error.status_code",
                HttpStatus.BAD_REQUEST.value());
        req.setAttribute("error", exception.getMessage());
        req.setAttribute("menus", this.menus);

        fillCommand(commands -> req.setAttribute("commands", commands));
        fillTournament(tournaments -> req.setAttribute("tournaments", tournaments));
        return "error";
    }

    @GetMapping({"/", "/index.html", "/home.html", "/compare.html"})
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

    private String compare(Optional<String> tournamentId, Optional<CompareFilter> filter, Model model) {
        List<Tournament> tournaments = fillModelWithInitialData(model);

        if (tournaments.size() == 1) {
            tournamentId = Optional.of(tournaments.get(0).getId());
        }

        CompareFilter compareFilter = fillFilter(tournamentId, filter);

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

    private CompareFilter fillFilter(Optional<String> tournamentId, Optional<CompareFilter> filter) {
        CompareFilter compareFilter = filter.orElseGet(() -> CompareFilter.builder().build());
        fillFilterField(tournamentId, tournament -> !isEmptyTrimmed(tournament), this::getTournament,
                                                                    Objects::nonNull, compareFilter::setTournament);
        fillFilterField(Optional.ofNullable(compareFilter.getSeason()), season -> !isEmptyTrimmed(season),
                                        t -> extractSeason(new Date()), Objects::nonNull, compareFilter::setSeason);
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

    private List<Tournament> fillModelWithInitialData(Model model) {
        model.addAttribute("menus", this.menus);
        fillCommand(commands -> model.addAttribute("commands", commands));
        return fillTournament(tournaments -> model.addAttribute("tournaments", tournaments));
    }

    private List<Tournament> fillTournament(Consumer<List<Tournament>> consumer) {
        List<Tournament> tournaments = dataServiceMongo.getTournaments();
        tournaments.sort((o1, o2) -> o1.getTournName().compareToIgnoreCase(o2.getTournName()));
        consumer.accept(tournaments);
        return tournaments;
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
}