package com.exemple.socialapp.service;


import com.exemple.socialapp.domain.Entity;
import com.exemple.socialapp.domain.User;
import com.exemple.socialapp.repository.Repository;
import com.exemple.socialapp.repository.paging.Page;
import com.exemple.socialapp.repository.paging.Pageable;
import com.exemple.socialapp.repository.paging.PagingRepository;

import java.util.Objects;
import java.util.Optional;

public class ServiceUsers<ID, E extends Entity<ID>>{
    private final PagingRepository<ID, E> repoUsers;
    public ServiceUsers(Repository<ID, E> repoUsers) {
        this.repoUsers = (PagingRepository<ID, E>) repoUsers;
    }

    public Optional<E> add(E entity) {
        return repoUsers.save(entity);
    }

    public Optional<E> remove(ID id) {
        return repoUsers.delete(id);
    }

    public Optional<E> update(E entity) {
        return repoUsers.update(entity);
    }

    public Optional<E> findOne(ID id) {
        return repoUsers.findOne(id);
    }

    public Iterable<E> findAll() {
        return repoUsers.findAll();
    }

    public boolean findUsersByUsername(String username) {
        boolean found = false;

        Iterable<E> allUsers = repoUsers.findAll();

        for (E user : allUsers) {
            if (Objects.equals(((User) user).getUsername(), username)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public Page<E> find_all(Pageable pageable) {
        return repoUsers.find_all(pageable);
    }

}