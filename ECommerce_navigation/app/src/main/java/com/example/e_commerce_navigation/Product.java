package com.example.e_commerce_navigation;

import java.util.Date;

/**
 * Class related to getting or setting DB Product Data.
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */

public class Product {
    private int id;
    private String title;
    private String description;
    private Double price;
    private String pic;
    private int category_id;
    private Date createdDTTM;
    private int stock;

    /**
     * Constructor of the class related to getting or setting DB Product Data.
     * @param id
     * @param title
     * @param description
     * @param price
     * @param pic
     * @param category_id
     * @param createdDTTM
     * @param stock
     */
    public Product(int id, String title, String description, Double price, String pic, int category_id, Date createdDTTM, int stock) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.pic = pic;
        this.category_id = category_id;
        this.createdDTTM = createdDTTM;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Date getCreatedDTTM() {
        return createdDTTM;
    }

    public void setCreatedDTTM(Date createdDTTM) {
        this.createdDTTM = createdDTTM;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
