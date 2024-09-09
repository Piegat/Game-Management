package com.dash.service;

import com.dash.entity.Player;
import com.dash.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    public PlayerRepository repository;


    public List<Player> getAll(){
        return repository.findAll();
    }

    public Player getById(Long id){
        return repository.findById(id).orElse(null);
    }

    public Player save(Player player){

        return repository.save(player);
    }

    public Player update(Player player){
        return repository.save(player);
    }

    public String delete(Long id){
        repository.deleteById(id);
        return "Deleted";
    }
}
