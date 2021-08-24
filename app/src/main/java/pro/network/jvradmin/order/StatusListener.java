package pro.network.jvradmin.order;

public interface StatusListener {

    void onDeliveredClick(String id);
    void onWhatsAppClick(String phone);
    void onCallClick(String phone);

}
