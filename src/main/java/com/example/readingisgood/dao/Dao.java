package com.example.readingisgood.dao;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

/**
 * Common interface for book and order dao classes
 * @param <T>: change the object type required in class
 */
public interface Dao<T>{

    Optional<T> get(String value); // get object from given value of field

    Collection<T> getAll(String condition) throws ParseException; // get all objects in list

    void save(T t); // save into mongo db

    void update(T t); // update object in mongo db

}
