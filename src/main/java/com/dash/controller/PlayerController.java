package com.dash.controller;

import com.dash.entity.Player;
import com.dash.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService service;

    @GetMapping("/all")
    private ResponseEntity<List<Player>> getAll(){
        try{
            List<Player> list = service.getAll();
            return ResponseEntity.ok(list);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<Player> getById(@PathVariable Long id){
        try{
            Player player = service.getById(id);
            return ResponseEntity.ok(player);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping()
    private ResponseEntity<String> create(@Validated @RequestBody Player player){
        try {
            service.save(player);
            return ResponseEntity.ok("Cadastrado com sucesso!");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping()
    private ResponseEntity<String> update(@Validated @RequestBody Player player){
        try {
            service.update(player);
            return ResponseEntity.ok("atualizado com sucesso");
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
