package pro.network.jvradmin.order;

import java.io.Serializable;
import java.util.ArrayList;

import pro.network.jvradmin.product.Product;

/**
 * Created by ravi on 16/11/17.
 */

public class Order implements Serializable {
    String id;
    String items;
    String quantity;
    String price;
    String status;
    String name;
    String phone;
    ArrayList<Product> productBeans;
    String createdOn;

    public Order() {
    }

    public Order(String items, String quantity, String price, String status, ArrayList<Product> productBeans) {
        this.items = items;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.productBeans = productBeans;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public ArrayList<Product> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(ArrayList<Product> productBeans) {
        this.productBeans = productBeans;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}