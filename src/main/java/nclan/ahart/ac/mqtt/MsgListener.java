package nclan.ahart.ac.mqtt;

/**
 * Interface that needs to be implemented for messages to be listened for.
 * @author ahart
 */
public interface MsgListener {
    void onMessageArrived(String topic, String message);
    void onMessageSent(String topic, Boolean success);
    void onMessageError(String message);
}
