package com.exemple.socialapp.repository.paging;

import com.exemple.socialapp.domain.Entity;
import com.exemple.socialapp.repository.Repository;

/**
 * A repository interface that supports pagination.
 *
 * @param <ID> The type of the entity identifier.
 * @param <E>  The type of the entity.
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * Retrieves a page of entities based on the provided pagination information.
     *
     * @param pageable The pagination information.
     * @return A page of entities.
     */
    Page<E> find_all(Pageable pageable);
}
