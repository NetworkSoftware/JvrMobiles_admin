package pro.network.jvradmin.feedback;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Feedback implements Serializable {
    String id;
    String user_name;
    String feedback;
    String user_phone;

    public Feedback() {
    }

    public Feedback(String user_name, String feedback) {
        this.user_name = user_name;
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }
}