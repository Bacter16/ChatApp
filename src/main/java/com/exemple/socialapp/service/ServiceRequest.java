package com.exemple.socialapp.service;

import com.exemple.socialapp.domain.Entity;
import com.exemple.socialapp.domain.FriendRequest;
import com.exemple.socialapp.domain.Friendship;
import com.exemple.socialapp.domain.Tuple;
import com.exemple.socialapp.repository.Repository;

import java.util.Optional;

public class ServiceRequest<ID, E extends Entity<ID>> {
    private final Repository<ID, E> repoRequest;

    public ServiceRequest(Repository<ID, E> repoRequest) {
        this.repoRequest = repoRequest;
    }

    public Optional<E> add(E entity) {
        Tuple<ID, ID> requestId = (Tuple<ID, ID>) ((FriendRequest) entity).getId();
        Optional<E> existingRequest = repoRequest.findOne((ID) requestId);



        if (existingRequest.isPresent()) {
            return Optional.empty();
        } else {
            return repoRequest.save(entity);
        }
    }

    public Optional<E> remove(ID id) {
        return repoRequest.delete(id);
    }

    public Optional<E> update(E entity) {
        return repoRequest.update(entity);
    }

    public Optional<E> findOne(ID id) {
        return repoRequest.findOne(id);
    }

    public Iterable<E> findAll() {
        return repoRequest.findAll();
    }

    public Optional<Friendship> accept(FriendRequest request){
        request.setStatus(FriendRequest.Status.valueOf("APPROVED"));

        if(repoRequest.update((E) request).isPresent()){
            Friendship f = new Friendship(request.getSenderId(), request.getReceiverId());
            return Optional.of(f);
        }
        return Optional.empty();
    }

    public Optional<FriendRequest> reject(FriendRequest request){
        request.setStatus(FriendRequest.Status.valueOf("REJECTED"));
        repoRequest.update((E) request).isPresent();
        return Optional.of(request);
    }

}
