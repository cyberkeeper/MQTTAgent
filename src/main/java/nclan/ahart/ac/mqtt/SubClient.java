package nclan.ahart.ac.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Class to keep a record of clients and topics subscribed to.
 * @author ahart
 */
public class SubClient {
    private MqttClient client = null;
    private String topic = "";

    public SubClient(MqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return topic;
    }
}
