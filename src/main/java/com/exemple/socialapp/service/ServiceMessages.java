package com.exemple.socialapp.service;



import com.exemple.socialapp.domain.Entity;
import com.exemple.socialapp.domain.Message;
import com.exemple.socialapp.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceMessages<ID, E extends Entity<ID>> {

    private final Repository<ID, E> repoMessages;

    public ServiceMessages(Repository<ID, E> repoMessages) {
        this.repoMessages = repoMessages;

    }

    public Optional<E> findOne(ID id){return repoMessages.findOne(id);}

    public Iterable<E> findAll(){
        return repoMessages.findAll();
    }

    public Optional<E> add(E entity){
        return repoMessages.save(entity);
    }

    public Optional<E> remove(ID id){
        return repoMessages.delete(id);
    }

    public Optional<E> update(E entity){
        return repoMessages.update(entity);
    }


    public List<E> findMessagesBetweenUsers(ID senderId, ID receiverId) {
        List<E> resultMessages = new ArrayList<>();

        repoMessages.findAll().forEach(message -> {
            if (message instanceof Message msg) {
                if (msg.getFrom_id().equals(senderId) && msg.getTo().contains(receiverId)) {
                    resultMessages.add(message);
                }
            }
        });
        return resultMessages;
    }
}
