package com.dash.service;

import com.dash.entity.Server;
import com.dash.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    @Autowired
    public ServerRepository repository;

    public List<Server> getAll(){
        return repository.findAll();
    }

    public Server getById(Long id){
        return repository.findById(id).orElse(null);
    }

    public Server save(Server server){
        return repository.save(server);
    }

    public Server update(Server server){
        return repository.save(server);
    }

    public String delete(Long id){
        repository.deleteById(id);
        return "Deleted";
    }
}
