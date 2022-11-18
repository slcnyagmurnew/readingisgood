package com.example.readingisgood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Document
public class Order {

    @Id
    private String id;

    private String username;

    private HashMap<String, Integer> books;

    private int bookCount;

    private double purchasedAmount;

    private Date orderDate;

    public Order(String username, HashMap<String, Integer> books, int bookCount, double purchasedAmount, Date orderDate) {
        this.username = username;
        this.books = books;
        this.bookCount = bookCount;
        this.purchasedAmount = purchasedAmount;
        this.orderDate = orderDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public HashMap<String, Integer> getBooks() {
        return books;
    }

    public void setBooks(HashMap<String, Integer> books) {
        this.books = books;
    }

    public int getBookCount() {
        return bookCount;
    }
    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public double getPurchasedAmount() {
        return purchasedAmount;
    }
    public void setPurchasedAmount(double purchasedAmount) {
        this.purchasedAmount = purchasedAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", books=" + books +
                ", bookCount=" + bookCount +
                ", purchasedAmount=" + purchasedAmount +
                ", orderDate=" + orderDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order order = (Order) obj;
        return Objects.equals(this.username, order.username) && Objects.equals(this.id, order.id) && Objects.equals(this.bookCount, order.bookCount)
                && Objects.equals(this.books, order.books) && Objects.equals(this.purchasedAmount, order.purchasedAmount);
    }
}
