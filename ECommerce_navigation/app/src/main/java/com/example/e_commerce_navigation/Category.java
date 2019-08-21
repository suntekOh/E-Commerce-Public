package com.example.e_commerce_navigation;

/**
 * Class related to getting or setting DB Category Data. *
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */

public class Category {
    private int id;
    private String descriptions;

    /** Constructor of the class related to getting or setting DB Category Data
     * @param id category code
     * @param descriptions category descriptions
     */
    public Category(int id, String descriptions) {
        this.id = id;
        this.descriptions = descriptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
}
