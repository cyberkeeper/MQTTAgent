import nclan.ahart.ac.mqtt.PubSubUI;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Entry point for the application.
 * @author ahart
 */
public class Main{

    private static ResourceBundle bundle;

    public static void main(String[] args) {
        System.out.println("MQTT Demo");
        Main myApp = new Main();
        myApp.showUI();
    }

    /**
     * All the commands to set up and show the user interface are included here
     */
    public void showUI() {
        Locale currentLocale = Locale.getDefault();
        String currentPath = System.getProperty("user.dir");
        System.out.println("Current path is:: " + currentPath);
        bundle = ResourceBundle.getBundle("message", currentLocale);
        //bundle = ResourceBundle.getBundle("message", new Locale("es"));
        JFrame myApp = new JFrame(bundle.getString("title"));
        myApp.setContentPane(new PubSubUI().mainAppPanel);
        myApp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        myApp.pack();
        //set size before setting location
        myApp.setSize(650, 350);
        myApp.setLocationRelativeTo(null);
        myApp.setVisible(true);
    }
}