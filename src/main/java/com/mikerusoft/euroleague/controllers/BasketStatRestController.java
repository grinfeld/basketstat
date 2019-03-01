package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.services.DataService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api")
public class BasketStatRestController {

    private DataService dataService;

    @Autowired
    public BasketStatRestController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/command/delete/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody Response deleteCommand(@PathVariable("id") int id) {
        dataService.deleteCommand(id);
        return Response.<Boolean>builder().data(true).build();
    }

    @GetMapping(value = "/tournament/delete/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody Response deleteTournament(@PathVariable("id") int id) {
        dataService.deleteTournament(id);
        return Response.<Boolean>builder().data(true).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> exceptionHandler(Exception ex, WebRequest request) {
        return new ResponseEntity<>(Response.<String>builder().data(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(builderClassName = "Builder", toBuilder = true)
    private static class Response<T> {
        private T data;
    }
}
