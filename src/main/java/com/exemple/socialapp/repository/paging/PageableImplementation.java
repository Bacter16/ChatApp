package com.exemple.socialapp.repository.paging;

public record PageableImplementation(int pageNumber, int pageSize) implements Pageable {

}
