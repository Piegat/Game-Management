package com.dash.controller;

import com.dash.entity.Server;
import com.dash.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/servers")
public class ServerController {

    @Autowired
    private ServerService service;

    @GetMapping("/all")
    private ResponseEntity<List<Server>> getAll(){
        try{
            List<Server> list = service.getAll();
            return ResponseEntity.ok(list);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<Server> getById(@PathVariable Long id){
        try{
            Server server = service.getById(id);
            return ResponseEntity.ok(server);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping()
    private ResponseEntity<String> create(@Validated @RequestBody Server server){
        try {
            service.save(server);
            return ResponseEntity.ok("Cadastrado com sucesso!");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping()
    private ResponseEntity<String> update(@Validated @RequestBody Server server){
        try {
            service.update(server);
            return ResponseEntity.ok("Atualizado com sucesso");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<String> delete(@PathVariable Long id){
        try{
            service.delete(id);
            return ResponseEntity.ok().body("Deletado com sucesso");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
