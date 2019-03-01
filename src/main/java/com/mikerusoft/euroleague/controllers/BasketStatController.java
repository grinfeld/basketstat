package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.menus.MenuProperties;
import com.mikerusoft.euroleague.model.Command;
import com.mikerusoft.euroleague.model.Result;
import com.mikerusoft.euroleague.model.Tournament;
import com.mikerusoft.euroleague.services.DataService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Controller
public class BasketStatController {

    private int matchesMaxNumber;
    private List<MenuProperties.Menu> menus;
    private DataService dataService;

    @Autowired
    public BasketStatController(@Value("${matches-max-number}") int matchesMaxNumber,
                                MenuProperties menu, DataService dataService) {
        this.menus = menu.getMenus();
        this.dataService = dataService;
        this.matchesMaxNumber = matchesMaxNumber;
    }

    @GetMapping({"/", "/index.html", "/home.html"})
    public String main(Model model) {
        fillModelWithInitialData(model);
        return "index";
    }

    @PostMapping({"/edit"})
    public String edit(@ModelAttribute("result") Result result, Model model) {
        fillModelWithInitialData(model);

        Integer commandId = result.getCommand() != null ? result.getCommand().getId() : null;
        Integer tournId = result.getTournament() != null ? result.getTournament().getId() : null;

        if (!fillModelWithCommandAndTournament(model, commandId, tournId)) return "index";

        Result updatedResult = dataService.saveResult(result);

        model.addAttribute("currentResult", updatedResult);
        model.addAttribute("setResult", true);

        model.addAttribute("msg", "Successfully updated result");

        return "index";
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
        fillCommand(model);
        fillTournament(model);
    }

    private void fillTournament(Model model) {
        List<Tournament> tournaments = dataService.getTournaments();
        model.addAttribute("tournaments", tournaments);
    }

    private void fillCommand(Model model) {
        List<Command> commands = dataService.getCommands();
        model.addAttribute("commands", commands);
    }

    @GetMapping({"/command.html"})
    public String command(Model model) {
        model.addAttribute("menus", this.menus);
        model.addAttribute("command", Command.builder().build());
        fillCommand(model);
        return "command";
    }

    @PostMapping({"/commandsave", "/commandsave/"})
    public String editCommand(@ModelAttribute("command") Command command, Model model) {
        model.addAttribute("menus", this.menus);

        command = command.getId() == null ? dataService.insertCommand(command.getCommandName()) :
                dataService.updateCommand(command);
        model.addAttribute("command", command);
        fillCommand(model);
        return "command";
    }

    @PostMapping({"/tournamentsave", "/tournamentsave/"})
    public String editTournament(@ModelAttribute("tourn") Tournament tournament, Model model) {
        model.addAttribute("menus", this.menus);


        tournament = tournament.getId() == null ? dataService.insertTournament(tournament.getTournName()) :
                dataService.updateTournament(tournament);
        model.addAttribute("tourn", tournament);
        fillTournament(model);
        return "tournament";
    }

    @GetMapping({"/tournament.html"})
    public String tournament(Model model) {
        model.addAttribute("menus", this.menus);
        model.addAttribute("tourn", Tournament.builder().build());
        fillTournament(model);
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