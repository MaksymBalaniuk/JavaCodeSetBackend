package com.javacodeset.service.api;

import java.util.List;

public interface CrudService<E, D, U> {
    E create(D dto);
    E get(U id);
    List<E> getAll();
    E update(D dto);
    void delete(U id);
}
