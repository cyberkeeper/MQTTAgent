package nclan.ahart.ac.mqtt;

public interface MsgListener {
    void onMessageArrived(String topic, String message);
    void onMessageSent(String topic, Boolean success);
    void onMessageError(String message);
}
