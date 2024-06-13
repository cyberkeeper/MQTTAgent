package nclan.ahart.ac.mqtt;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.*;

/**
 * Helper methods for connecting, publishing and connecting to MQTT broker.
 * @author ahart
 * A dependency has been added to the pom.xml file for the required library.
 * <dependency>
 * <groupId>org.eclipse.paho</groupId>
 * <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
 * <version>1.2.5</version>
 * </dependency>
 */
public class Pubsub {
   // protected String topic = "topic/test";
    /**
     * Subscription QoS
     * 0, at most once. Message could be lost.
     * 1, at least once. Need msg to be received but subscriber can not handle duplicates.
     * 2, exactly one. Need msg to be received but subscriber CAN handle duplicates.
     */
    protected int subQos = 1;
    /**
     * Publishing QoS
     * 0, at most once. Message could be lost.
     * 1, at least once. Need msg to be received but subscriber can not handle duplicates.
     * 2, exactly one. Need msg to be received but subscriber CAN handle duplicates.
     */
    protected int pubQos = 1;

    /**
     * Create client to connect to the MQTT broker
     *
     * @param broker   the broker to connect to
     * @param clientId identifier to uniquely identify this client.
     * @return MqttClient if connection was made, else null
     */
    public MqttClient connectToBroker(String broker, String clientId, String user, char[] passwd) throws MqttException{
        MqttClient client = null;
        try {
            //if client id is null or blank generate a random identifier
            if (clientId == null || clientId.isBlank())
                clientId = UUID.randomUUID().toString();

            //create client, does not connect automatically
            client = new MqttClient(broker, clientId);

            //setup any options required and connect
            MqttConnectOptions options = new MqttConnectOptions();
            //options.setAutomaticReconnect(true);
            //options.setCleanSession(true);
            //options.setConnectionTimeout(10);
            options.setUserName(user);
            options.setPassword(passwd);
            client.connect(options);

            //was connection successful?
            if (client.isConnected()) {
                return client;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Disconnect from the supplied client, also closes the client.
     *
     * @param client MqTT client to disconnect from broker.
     * @return True is connection successful else false. Will also return true if the supplied client was null or
     * not connected.
     */
    public boolean disconnectBroker(MqttClient client) {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                client.close();
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }

    /**
     * Send message to the client.
     *
     * @param client MQtt client that the message will be sent via.
     * @param msg    message to be sent, no checks are made on the supplied string.
     */
    public void sendMsg(MqttClient client, String topic, String msg) {

        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(msg.getBytes());
                message.setQos(pubQos);     //set quality of service
                //message.setRetained(true);  //set msg to be kept until read by a subscriber
                client.publish(topic, message);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param client
     * @return
     */
    public MqttMessage getMsg(MqttClient client) {
        final MqttMessage[] gotMsg = {null};
        try {
            if (client != null && client.isConnected()) {
                client.setCallback(new MqttCallback() {
                    //listens for a msg arrived event, gets the content of the received message
                    public void messageArrived(String topic, MqttMessage message) {
                        //System.out.println("topic: " + topic);
                        //System.out.println("qos: " + message.getQos());
                        //System.out.println("message content: " + new String(message.getPayload()));
                        gotMsg[0] = message;
                    }
                    //listens for connection lost event
                    public void connectionLost(Throwable cause) {

                        System.out.println("connectionLost: " + cause.getMessage());
                    }
                    //listens for delivery complete event
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("deliveryComplete: " + token.isComplete());
                    }
                });
                client.subscribe("update", subQos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gotMsg[0];
    }
}
