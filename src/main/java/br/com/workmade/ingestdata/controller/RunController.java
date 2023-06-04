package br.com.workmade.ingestdata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class RunController {

    @GetMapping("/run")
    public ResponseEntity<Void> run(){
        return ResponseEntity.ok().build();
    }
}
