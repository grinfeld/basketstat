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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api")
public class BasketStatRestController {

    private DataService<Integer> dataService;

    @Autowired
    public BasketStatRestController(DataService<Integer> dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/command/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response deleteCommand(@PathVariable("id") int id) {
        dataService.deleteCommand(id);
        return Response.<Boolean>builder().data(true).build();
    }

    @GetMapping(value = "/tournament/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response deleteTournament(@PathVariable("id") int id) {
        dataService.deleteTournament(id);
        return Response.<Boolean>builder().data(true).build();
    }

    @GetMapping(value = "/export", headers = "Accept=*/*", produces = "application/vnd.ms-excel")
    public void export(HttpServletResponse response, OutputStream outputStream) throws IOException {
        response.setHeader("Content-Disposition", "form-data; name=\"Content-Disposition\"; filename=\"" + System.currentTimeMillis() + "_export.csv" + "\"");
        dataService.writeAllResults(outputStream);
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
