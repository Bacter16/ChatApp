package com.exemple.socialapp.service;

import com.exemple.socialapp.domain.Entity;
import com.exemple.socialapp.domain.User;
import com.exemple.socialapp.repository.Repository;

import java.util.List;
import java.util.Optional;

public class ServiceFriendship<ID, E extends Entity<ID>>{
    private final Repository<ID, E> repoFriendship;
    public ServiceFriendship(Repository<ID, E> repoFriendship) {
        this.repoFriendship = repoFriendship;
    }

    public Optional<E> add(E entity) {
        return repoFriendship.save(entity);
    }

    public Optional<E> remove(ID id) {
        return repoFriendship.delete(id);
    }

    public Optional<E> update(E entity) {
        return repoFriendship.update(entity);
    }

    public Optional<E> findOne(ID id) {
        return repoFriendship.findOne(id);
    }

    public Iterable<E> findAll() {
        return repoFriendship.findAll();
    }

    public List<User> findAllFriends(ID id) {
        return null;
    }
}
