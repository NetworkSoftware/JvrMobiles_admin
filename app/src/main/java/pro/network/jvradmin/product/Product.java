package pro.network.jvradmin.product;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Product implements Serializable {
    String id;
    String brand;
    String model;
    String price;
    String ram;
    String rom;
    String name;
    String image;
    String description;
    String category;
    String qty;
    String stock_update;
    String userId;

    public Product() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Product(String brand, String model, String price, String ram, String rom, String name, String image, String description, String category) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.ram = ram;
        this.rom = rom;
        this.name = name;
        this.image = image;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStock_update() {
        return stock_update;
    }

    public void setStock_update(String stock_update) {
        this.stock_update = stock_update;
    }
}