package nclan.ahart.ac.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Class bound to the PubSubUI designer form.
 *
 * @author ahart
 */
public class PubSubUI {
    private JTabbedPane tabbedPane1;
    public JPanel mainAppPanel;
    private JTextField txtBroker;
    private JTextField txtID;
    private JButton btnGenerate;
    private JButton btnConnect;
    private JPanel pubMsgPanel;
    private JTextArea txtMsgs;
    private JButton btnPublish;
    private JLabel lblStatus;
    private JPanel subMessagePanel;
    private JTextArea txtSubMsgs;
    private JButton btnSub;
    private JButton btnUnSub;
    private JTextField txtPort;
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private JPanel Connector;
    private JComboBox cbTopics;
    private JComboBox cbSubscribedTopics;
    private JPanel pubConnectPanel;
    private JButton btnClear;
    private Pubsub conn = new Pubsub();
    private MqttClient client;

    private ArrayList<SubClient> subscribers = new ArrayList<>();

    /**
     * Constructor sets up the action listeners.
     */
    public PubSubUI() {
        btnGenerate.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                txtID.setText(UUID.randomUUID().toString());
            }
        });
        btnConnect.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client != null && client.isConnected()) {
                    try {
                        client.disconnect();
                        client.close();
                        client = null;
                        lblStatus.setText("Disconnected");
                        updateEditables(true);
                        btnConnect.setText("Connect");
                    } catch (MqttException ex) {
                        //throw new RuntimeException(ex);
                        lblStatus.setText(ex.getMessage());
                    }
                } else {
                    try {
                        client = setupConn(txtBroker.getText(), txtPort.getText(), txtID.getText(), txtUserId.getText(), txtPassword.getPassword());
                        lblStatus.setText(client.getServerURI());
                        updateEditables(false);
                        btnConnect.setText("Disconnect");
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(pubMsgPanel, err.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        btnPublish.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String topic = cbTopics.getSelectedItem().toString();
                    conn.sendMsg(client, topic, txtMsgs.getText().strip());
                    cbTopics.addItem(topic);
                    JOptionPane.showMessageDialog(pubMsgPanel, "Message published", "Information", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(pubMsgPanel, err.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnSub.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String topic = cbSubscribedTopics.getSelectedItem().toString();
                setupSubConn(client, topic);
            }
        });
        btnUnSub.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnClear.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSubMsgs.setText("");
            }
        });
    }

    /**
     * Helper method to disable/enable multiple different components
     *
     * @param val if true the components are editable else they aren't
     */
    private void updateEditables(boolean val) {
        txtBroker.setEnabled(val);
        txtPort.setEnabled(val);
        txtUserId.setEnabled(val);
        txtPassword.setEnabled(val);
        txtID.setEnabled(val);
        btnGenerate.setEnabled(val);
    }

    /**
     * Get a connection to the MQTT broker specified in the GUI
     *
     * @param broker
     * @param port
     * @param clientID
     * @param user
     * @param password
     * @return Return connected client or null if something went wrong
     * @throws Exception
     */
    private MqttClient setupConn(String broker, String port, String clientID, String user, char[] password) throws Exception {
        //cleanup supplied data, strip off any leading or trailing spaces
        broker = broker.toLowerCase().strip();
        clientID = clientID.strip();
        port = port.strip();
        MqttClient client = null;

        //check that we have what is needed to connect
        String err = "Incorrect data supplied";
        boolean exc = false;
        if (broker.isBlank()) {
            err = "Broker must be supplied";
            exc = true;
        } else {
            broker = "tcp://" + broker + ":" + port;
        }
        if (clientID.isBlank()) {
            err = "Unique identifier must be supplied";
            exc = true;
        }

        //no errors, try connecting
        if (!exc) {
            try {
                client = conn.connectToBroker(broker, clientID, user, password);
            } catch (MqttException me) {
                err = me.getMessage();
                exc = true;
            }
        }

        //report any errors
        if (exc) {
            throw new Exception(err);
        }
        return client;
    }

    /**
     * Get a connection to the MQTT broker specified in the GUI for subscribing
     *
     * @return Return connected client or null if something went wrong
     */
    private MqttClient setupSubConn(MqttClient client, String topic) {
        String err = "Incorrect data supplied";
        boolean exc = false;
        try {
            if (client != null && client.isConnected()) {
                getMsg(client, topic);
                //SubClient newClient = new SubClient(client, (tfSubTopic.getText()));
                //subscribers.add(newClient);
                //cbSubcribedTo.addItem(newClient);
            }

        } catch (Exception me) {
            err = me.getMessage();
            exc = true;
        }


        //report any errors
        if (exc) {
            JOptionPane.showMessageDialog(subMessagePanel, err, "Error", JOptionPane.WARNING_MESSAGE);
        }
        return client;
    }


    public void getMsg(MqttClient client, String topic) throws Exception {
        try {
            if (client != null && client.isConnected()) {
                client.setCallback(new MqttCallback() {
                    //listens for a msg arrived event, gets the content of the received message
                    public void messageArrived(String topic, MqttMessage message) {
                        StringBuilder sbMsg = new StringBuilder(topic).append(": ");
                        sbMsg.append(new String(message.getPayload()) + "\n");
                        txtSubMsgs.append(sbMsg.toString());
                    }

                    //listens for connection lost event
                    public void connectionLost(Throwable cause) {
                        JOptionPane.showMessageDialog(subMessagePanel, cause.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                        //throw new Exception(cause.getMessage());
                    }

                    //listens for delivery complete event
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        JOptionPane.showMessageDialog(subMessagePanel, "deliveryComplete: " + token.isComplete(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                client.subscribe(topic, 1);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(subMessagePanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        cbTopics = new JComboBox<>(new UniqueComboBoxModel<>());
        cbSubscribedTopics = new JComboBox(new UniqueComboBoxModel());
    }

    class UniqueComboBoxModel<T> extends DefaultComboBoxModel<T> {

        private final Set<T> uniqueItems = new HashSet<>();

        @Override
        public void addElement(T item) {
            if (item != null && uniqueItems.add(item)) {
                super.addElement(item);
            }
        }
    }
}
