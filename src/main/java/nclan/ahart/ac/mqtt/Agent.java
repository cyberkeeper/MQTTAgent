package nclan.ahart.ac.mqtt;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.sound.sampled.*;

/**
 * Starting class for the application. Starts the Graphical User Interface. Application will default to English language
 * unless the Locale is detected as Spain or code is forced to be in Spanish.
 * Success and failure sounds are royalty free and were downloaded from Pixabay, https://pixabay.com/sound-effects/search/?duration=0-30.
 * MP3 files converted to wav using online converter tool, see https://www.freeconvert.com/audio-converter
 *
 * @author ahart
 */
public class Agent {

    public static ResourceBundle bundle;
    public static final String SUCCESS_SOUND = "/sounds/success-1-6297.wav";
    public static final String FAILURE_SOUND = "/sounds/failure-1-89170.wav";

    /**
     * Main entry point to the application
     *
     * @param args None expected
     */
    public static void main(String[] args) {
        Agent myApp = new Agent();
        myApp.showUI();
    }

    /**
     * All the commands to set up and show the user interface are included here.
     * For testing that internalisation works comment out the Locale.getDefault line and uncomment the
     * Locale.forLanguageTag("es") line, or leave the default Locale line in place and change the Microsoft Windows
     * Locale to es.
     */
    public void showUI() {
        /*find out or set which locale are we running from, default should be en_GB, use the es locale for testing
        only uncomment one of the following lines.
         */
        //Locale whereAmI = Locale.getDefault();
        //Locale whereAmI = Locale.forLanguageTag("es");

        //set the default location for the Java virtual machine
        //Locale.setDefault(whereAmI);

        try {
            bundle = setupLocale("message");

            //get the resource bundle appropriate for the current Locale.
            // bundle = ResourceBundle.getBundle("message", whereAmI);

            //create the JFrame to hold everything
            JFrame myApp = new JFrame(bundle.getString("title"));

            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/cat.png"));
            myApp.setIconImage(image);

            //add the form created within IntelliJ form designer
            myApp.setContentPane(new PubSubUI().mainAppPanel);
            //set default close operation for when the window close icon is pressed
            myApp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            myApp.pack();
            //set size before setting window on screen location
            myApp.setSize(650, 350);
            myApp.setLocationRelativeTo(null);
            myApp.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }

        //debugging helpers
        //String currentPath = System.getProperty("user.dir");
        //System.out.println("Current path is:: " + currentPath);
    }

    /**
     * Set up the resource bundle required for this application.
     *
     * @return ResourceBundle to be used
     */
    public static ResourceBundle setupLocale(String bundleName) throws MissingResourceException {
        ResourceBundle bundle = null;
        try {
        /*find out or set which locale are we running from, default should be en_GB, use the es locale for testing
        only uncomment one of the following lines.
         */
            //Locale whereAmI = Locale.getDefault();
            Locale whereAmI = Locale.forLanguageTag("es");

            //set the default location for the Java virtual machine
            Locale.setDefault(whereAmI);

            //get the resource bundle appropriate for the current Locale.
            bundle = ResourceBundle.getBundle(bundleName, whereAmI);
        } catch (MissingResourceException mre) {
            //the asked for resource bundle can't be found abort program.
            throw new MissingResourceException("Missing resources, unable to continue", bundleName, "");
        } catch (NullPointerException npe) {
            //something went wrong, either resource bundle or locale have not been found. Set the locale to default
            bundle = ResourceBundle.getBundle(bundleName);
        }
        return bundle;
    }

    public static void playSound(String soundPath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Agent.class.getResource(soundPath));
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            byte[] soundBytes = new byte[audioStream.available()];
            int nBytesRead;
            while ((nBytesRead = audioStream.read(soundBytes)) != -1) {
                line.write(soundBytes, 0, nBytesRead);
            }
            line.drain();
            line.close();
            audioStream.close();
        } catch (Exception e) {
            //failure to play sound effect isn't important, catch error, log it and continue
            System.out.println("Sound error: " + e.getMessage());
        }
    }

    /**
     * Play successful sound. If something goes wrong with sound effect nothing will be played.
     */
    public static void playSuccessSound() {
        playSound(SUCCESS_SOUND);
    }

    /**
     * Play failure sound. If something goes wrong with sound effect nothing will be played.
     */
    public static void playFailureSound() {  // Similar logic for error sound
        playSound(FAILURE_SOUND);
    }
}