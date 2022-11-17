package com.example.readingisgood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Month;

@Document
public class Stats {

    @Id
    private String id;

    private final int totalBookCount;

    private final int totalOrderCount;

    private final double totalPurchasedAmount;

    public Stats(String id, int totalBookCount, double totalPurchasedAmount, int totalOrderCount) {
        this.id = numberToMonthName(Integer.parseInt(id));
        this.totalBookCount = totalBookCount;
        this.totalOrderCount = totalOrderCount;
        this.totalPurchasedAmount = totalPurchasedAmount;
    }

    public int getTotalBookCount() {
        return totalBookCount;
    }

    public int getTotalOrderCount() {
        return totalOrderCount;
    }

    public double getTotalPurchasedAmount() {
        return totalPurchasedAmount;
    }


    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "id='" + id + '\'' +
                ", totalBookCount=" + totalBookCount +
                ", totalOrderCount=" + totalOrderCount +
                ", totalPurchasedAmount=" + totalPurchasedAmount +
                '}';
    }

    /**
     * returns month name by index of month
     * @param monthNumber month index
     * @return month name
     */
    public String numberToMonthName(int monthNumber) {
        return Month.of(monthNumber).name();
    }
}
