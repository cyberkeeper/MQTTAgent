package nclan.ahart.ac.mqtt;

import javax.sound.sampled.*;

/**
 * This class plays a audio wav file. It extends Thread so that the sound file is loaded and played within a
 * separate thread, this prevents the main application having to wait for the audio file to complete.
 * @author ahart
 */
public class PlaySound extends Thread{
    private final String playThis;

    /**
     * Default constructor
     * @param sound The sound file to be played.
     */
    public PlaySound(String sound) {
        playThis = sound;
    }

    /**
     * This is what the thread will actually do.
     */
    @Override
    public void run()
    {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Agent.class.getResource(playThis));
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            byte[] soundBytes = new byte[audioStream.available()];
            int nBytesRead =-1;
            while ((nBytesRead = audioStream.read(soundBytes)) != -1) {
                line.write(soundBytes, 0, nBytesRead);
            }
            line.drain();
            line.close();
            audioStream.close();
        } catch (Exception e) {
            //failure to play sound effect isn't important, catch error and continue
        }
    }
}
