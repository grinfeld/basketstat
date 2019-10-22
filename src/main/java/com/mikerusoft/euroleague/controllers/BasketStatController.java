package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.menus.MenuProperties;
import com.mikerusoft.euroleague.model.Command;
import com.mikerusoft.euroleague.model.Result;
import com.mikerusoft.euroleague.model.Tournament;
import com.mikerusoft.euroleague.services.DataService;
import com.mikerusoft.euroleague.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

@Controller
public class BasketStatController {

    private int matchesMaxNumber;
    private List<MenuProperties.Menu> menus;
    private DataService<Integer> dataService;

    @Autowired
    public BasketStatController(@Value("${matches-max-number}") int matchesMaxNumber,
                                MenuProperties menu, DataService<Integer> dataService) {
        this.menus = menu.getMenus();
        this.dataService = dataService;
        this.matchesMaxNumber = matchesMaxNumber;
    }

    @GetMapping({"/", "/index.html", "/home.html"})
    public String main(Model model) {
        fillModelWithInitialData(model);
        return "index";
    }

    private static void assertResult(Result result) {
        int sunOfSplitScoreIn = result.getScored1Points() + result.getScored2Points() * 2 + result.getScored3Points() * 3;
        if (sunOfSplitScoreIn != result.getScoreIn()) {
            throw new IllegalArgumentException("Sum of scored points should be the same as score in");
        }

        if (result.getScored1Points() > result.getAttempts1Points()) {
            throw new IllegalArgumentException("Scored 1 point should be less or equal to attempts");
        }

        if (result.getScored2Points() > result.getAttempts2Points()) {
            throw new IllegalArgumentException("Scored 2 point should be less or equal to attempts");
        }

        if (result.getScored3Points() > result.getAttempts3Points()) {
            throw new IllegalArgumentException("Scored 3 point should be less or equal to attempts");
        }
    }

    private static class AssertResultException extends RuntimeException {
        private String fromPage;

        AssertResultException(String message, String fromPage) {
            super(message);
            this.fromPage = fromPage;
        }
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
        return "".equals(exception.fromPage) ? "error" : exception.fromPage;
    }

    @PostMapping({"/edit"})
    public String edit(@ModelAttribute("result") Result result, Model model) {
        fillModelWithInitialData(model);

        Integer commandId = result.getCommand() != null ? Utils.parseIntWithDeNull(result.getCommand().getId()) : null;
        Integer tournId = result.getTournament() != null ? Utils.parseIntWithDeNull(result.getTournament().getId()) : null;

        if (!fillModelWithCommandAndTournament(model, commandId, tournId)) return "index";
        try {
            assertResult(result);
        } catch (Exception e) {
            throw new AssertResultException(e.getMessage(), "index");
        }

        result = normalizeData(result);

        Result updatedResult = dataService.saveResult(result);

        model.addAttribute("currentResult", updatedResult);
        model.addAttribute("setResult", true);

        model.addAttribute("msg", "Successfully updated result");

        return "index";
    }

    private static Result normalizeData(Result result) {
        Date date = result.getDate();
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        instance.setTime(date);

        return result.toBuilder().date(new Date(instance.getTime().getTime())).build();
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
                    .tournament(tournId != null ? dataService.getTournament(tournId) : null)
                    .build()
                );
                needLastResults = false;
                break;
        }

        return needLastResults;
    }

    private boolean fillModelWithCommandAndTournament(Model model, Integer commandId, Integer tournId) {
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

        ResultAvg avg = aggregateResults(lastResults);

        model.addAttribute("results1st", lastResults);
        model.addAttribute("avg1st", avg);
    }

    private static Integer convertString(String str) {
        return StringUtils.isEmpty(str) ? null : (str.trim().replaceAll("\\d", "").equals("") ? Integer.parseInt(str.trim()) : null);
    }

    private static ResultAvg aggregateResults(List<Result> lastResults) {
        ResultStat rs = new ResultStat();
        for (Result r : lastResults) {
            rs.increaseAttempts1Points(r.getAttempts1Points());
            rs.increaseScored1Points(r.getScored1Points());
            rs.increaseAttempts2Points(r.getAttempts2Points());
            rs.increaseScored2Points(r.getScored2Points());
            rs.increaseAttempts3Points(r.getAttempts3Points());
            rs.increaseScored3Points(r.getScored3Points());
            rs.increaseScoreIn(r.getScoreIn());
            rs.increaseScoreOut(r.getScoreOut());
        }

        return ResultAvg.builder().points1(Math.round(10000d * rs.getScored1Points() / rs.getAttempts1Points()) / 100d)
                .points2(Math.round(10000d * rs.getScored2Points() / rs.getAttempts2Points()) / 100d)
                .points3(Math.round(10000d * rs.getScored3Points() / rs.getAttempts3Points()) / 100d)
                .scoreIn(Math.round(10d * rs.getScoreIn() / lastResults.size()) / 10d)
                .scoreOut(Math.round(10d * rs.getScoreOut() / lastResults.size()) / 10d)
                .build();
    }

    private void fillModelWithInitialData(Model model) {
        model.addAttribute("menus", this.menus);
        fillCommand(commands -> model.addAttribute("commands", commands));
        fillTournament(tournaments -> model.addAttribute("tournaments", tournaments));
    }

    private void fillTournament(Consumer<List<Tournament>> consumer) {
        List<Tournament> tournaments = dataService.getTournaments();
        consumer.accept(tournaments);
        //model.addAttribute("tournaments", tournaments);
    }

    private void fillCommand(Consumer<List<Command>> consumer) {
        List<Command> commands = dataService.getCommands();
        consumer.accept(commands);
        //model.addAttribute("commands", commands);
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

        command = command.getId() == null ? dataService.insertCommand(command.getCommandName()) :
                dataService.updateCommand(command);
        model.addAttribute("command", command);
        fillCommand(commands -> model.addAttribute("commands", commands));
        return "command";
    }

    @PostMapping({"/tournamentsave", "/tournamentsave/"})
    public String editTournament(@ModelAttribute("tourn") Tournament tournament, Model model) {
        model.addAttribute("menus", this.menus);

        tournament = tournament.getId() == null ? dataService.insertTournament(tournament.getTournName()) :
                dataService.updateTournament(tournament);
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

    private enum Action {
        delete, edit, create,
        na;

        public static Action byName(String name) {
            name = name == null ? "" : name.trim();
            try {
                return Action.valueOf(name.toLowerCase());
            } catch (Exception ignore){}

            return na;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(builderClassName = "Builder")
    public static class ResultAvg {
        private double points3;
        private double points2;
        private double points1;
        private double scoreIn;
        private double scoreOut;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ResultStat {
        private int attempts3Points;
        private int scored3Points;
        private int attempts2Points;
        private int scored2Points;
        private int attempts1Points;
        private int scored1Points;
        private int scoreIn;
        private int scoreOut;

        public void increaseScoreIn(int scoreIn) {
            this.scoreIn = this.scoreIn + scoreIn;
        }

        public void increaseScoreOut(int scoreOut) {
            this.scoreOut = this.scoreOut + scoreOut;
        }

        public void increaseAttempts3Points(int attempts3Points) {
            this.attempts3Points = this.attempts3Points + attempts3Points;
        }

        public void increaseScored3Points(int scored3Points) {
            this.scored3Points = this.scored3Points + scored3Points;
        }

        public void increaseAttempts2Points(int attempts2Points) {
            this.attempts2Points = this.attempts2Points + attempts2Points;
        }

        public void increaseScored2Points(int scored2Points) {
            this.scored2Points = this.scored2Points + scored2Points;
        }

        public void increaseAttempts1Points(int attempts1Points) {
            this.attempts1Points = this.attempts1Points + attempts1Points;
        }

        public void increaseScored1Points(int scored1Points) {
            this.scored1Points = this.scored1Points + scored1Points;
        }
    }

}