package com.exemple.socialapp.repository.paging;


import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> {
    private final Pageable pageable;
    private final Iterable<E> elements;

    public Paginator(Pageable pageable, Iterable<E> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }

    public Page<E> paginate() {
        Stream<E> result = StreamSupport.stream(elements.spliterator(), false)
                .skip((long) pageable.pageNumber() * pageable.pageSize())
                .limit(pageable.pageSize());
        return new PageImplementation<>(pageable, result);
    }
}
