package nclan.ahart.ac.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Class bound to the PubSubUI designer form.
 *
 * @author ahart
 */
public class PubSubUI implements MsgListener {
    private JTabbedPane tabbedPane1;
    public JPanel mainAppPanel;  //public to allow creation from else where.
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
    private JPanel ConnectDetailPanel;
    private JButton btnClear;
    private Pubsub conn = new Pubsub();
    private MqttClient client;

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
                Agent.playSuccessSound();
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
                handleConnect();
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
                handlePublish();
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
                handleSubscribe();
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
                handleUnSubscribe();
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
     * Connect to the specified MQTT broker
     */
    private void handleConnect() {
        if (client != null && client.isConnected()) {
            //if already connected disconnect
            try {
                client.disconnect();
                client.close();
                client = null;
                lblStatus.setText(Agent.bundle.getString("disconnected"));
                updateEditables(true);
                btnConnect.setText(Agent.bundle.getString("connect"));
            } catch (MqttException ex) {
                onMessageError(ex.getMessage());
                lblStatus.setText(ex.getMessage());
            }
        } else {
            //not connected, so connect
            try {
                client = setupConn(txtBroker.getText(), txtPort.getText(), txtID.getText(), txtUserId.getText(), txtPassword.getPassword());
                lblStatus.setText(client.getServerURI());
                updateEditables(false);
                btnConnect.setText(Agent.bundle.getString("disconnect"));
            } catch (Exception err) {
                onMessageError(err.getMessage());
            }
        }
    }

    /**
     * Publish the supplied text to the specified topic on the broker
     */
    private void handlePublish() {
        try {
            String topic = cbTopics.getSelectedItem().toString();
            conn.sendMsg(client, topic, txtMsgs.getText().strip());
            cbTopics.addItem(topic);
            onInfo(Agent.bundle.getString("msgPublished"));
        } catch (Exception err) {
            onMessageError(err.getMessage());
        }
    }

    /**
     * Subscribe to the supplied topic on the connected broker
     */
    private void handleSubscribe() {
        String topic = cbSubscribedTopics.getSelectedItem().toString();
        setupSubscription(client, topic);
    }

    /**
     * Unsubscribe to the supplied topic on the connected broker
     */
    private void handleUnSubscribe() {

    }

    /**
     * Add the supplied topic and message to the JTextArea
     *
     * @param topic Topic the message arrived on
     * @param msg   The message that arrived
     */
    private void handleMsgArrived(String topic, String msg) {
        StringBuilder sbMsg = new StringBuilder(topic).append(": ");
        sbMsg.append(new String(msg) + "\n");
        txtSubMsgs.append(sbMsg.toString());
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
     * @param broker   MQTT broker, supply IP address or hostname
     * @param port     port number for broker
     * @param clientID unique identifier for the client trying to connect
     * @param user     if authentication is set up supply a username
     * @param password if authentication is set up supply a password
     * @return Return connected client or null if something went wrong
     * @throws Exception If any error occurred an exception is thrown
     */
    private MqttClient setupConn(String broker, String port, String clientID, String user, char[] password) throws Exception {
        //cleanup supplied data, strip off any leading or trailing spaces
        broker = broker.toLowerCase().strip();
        clientID = clientID.strip();
        port = port.strip();
        MqttClient client = null;

        //check that we have what is needed to connect
        String err = Agent.bundle.getString("badData");
        boolean exc = false;
        if (broker.isBlank()) {
            err = Agent.bundle.getString("badBroker");
            exc = true;
        } else {
            broker = "tcp://" + broker + ":" + port;
        }
        if (clientID.isBlank()) {
            err = Agent.bundle.getString("badID");
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
    private MqttClient setupSubscription(MqttClient client, String topic) {
        String err = Agent.bundle.getString("badData");
        boolean exc = false;
        try {
            if (client != null && client.isConnected()) {
                conn.getMsg(client, topic, this);
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
            this.onMessageError(err);
        }
        return client;
    }

    /**
     * Setup bespoke components
     */
    private void createUIComponents() {
        //going to use a bespoke version of the JCombobox that does not allow duplicates.
        cbTopics = new JComboBox<>(new UniqueComboBoxModel<>());
        cbSubscribedTopics = new JComboBox(new UniqueComboBoxModel());
    }

    /**
     * Listen for a message arriving to a subscribed topic event.
     *
     * @param topic   Topic that message arrived on
     * @param message Message that arrived
     */
    @Override
    public void onMessageArrived(String topic, String message) {
        handleMsgArrived(topic, message);
    }

    /**
     * Listen for a delivery complete event.
     *
     * @param topic   Topic that message arrived on
     * @param success True if message delivery successfully, else false
     */
    @Override
    public void onMessageSent(String topic, Boolean success) {
        JOptionPane.showMessageDialog(mainAppPanel, Agent.bundle.getString("deliveryComplete: ") + Boolean.toString(success), Agent.bundle.getString("information"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Listen for an error event. Also used to generally show error messages when they occur
     *
     * @param message Error message
     */
    @Override
    public void onMessageError(String message) {
        JOptionPane.showMessageDialog(mainAppPanel, message, "Error", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Listen for an information event. Also used to generally show error messages when they occur
     *
     * @param message Information event
     */
    public void onInfo(String message) {
        JOptionPane.showMessageDialog(mainAppPanel, message, Agent.bundle.getString("information"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Bespoke version of ComboBoxModel that does not allow duplicate entries to be added
     * @param <T> Element to be added to the model
     */
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
