package pro.network.jvradmin.banner;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Banner implements Serializable {
    String id;
    String banner;
    String description;
    String type;

    public Banner() {
    }

    public Banner(String banner, String description, String type) {
        this.banner = banner;
        this.description = description;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}