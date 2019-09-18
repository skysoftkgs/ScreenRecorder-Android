package sim.ami.com.myapplication;

import java.io.File;

public class VideoModel {
    private String time;
    private File file;

    public VideoModel(String time, File file) {
        this.time = time;
        this.file = file;
    }

    public String getTime() {
        return time;
    }

    public File getFile() {
        return file;
    }
}
