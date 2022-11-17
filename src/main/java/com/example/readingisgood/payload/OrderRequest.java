package com.example.readingisgood.payload;

import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.HashMap;

/**
 * Standardized class to accept operations of ordering
 */
public class OrderRequest {

    private HashMap<String, Integer> books;

    private String id;

    private Date start;

    private Date end;

    public HashMap<String, Integer> getBooks() {
        return books;
    }

    public void setBooks(HashMap<String, Integer> books) {
        this.books = books;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
