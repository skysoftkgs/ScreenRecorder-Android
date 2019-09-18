package com.ami.com.ami.utils;

/**
 * Created by hi on 5/11/16.
 */
public class Config {
    private String resolution;
    private String frameRate;
    private String bitRate;
    private String orientation;
    private boolean isEnableRecordAudio;
    private boolean isEnableShowCamera;
    private boolean isEnableAutoRecord;
    private boolean isEnableShowTouches;
    private boolean isEnableCountdown;
    private String countDownValue;

    public Config(String resolution, String frameRate, String bitRate, String orientation, boolean isEnableRecordAudio, boolean isEnableShowCamera, boolean isEnableAutoRecord,boolean isEnableShowTouches, boolean isEnableCountdown, String countDownValue) {
        this.resolution = resolution;
        this.frameRate = frameRate;
        this.bitRate = bitRate;
        this.orientation = orientation;
        this.isEnableRecordAudio = isEnableRecordAudio;
        this.isEnableShowCamera = isEnableShowCamera;
        this.isEnableAutoRecord = isEnableAutoRecord;
        this.isEnableShowTouches = isEnableShowTouches;
        this.isEnableCountdown = isEnableCountdown;
        this.countDownValue = countDownValue;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public boolean isEnableRecordAudio() {
        return isEnableRecordAudio;
    }

    public void setEnableRecordAudio(boolean enableRecordAudio) {
        isEnableRecordAudio = enableRecordAudio;
    }

    public boolean isEnableShowCamera() {
        return isEnableShowCamera;
    }

    public void setEnableShowCamera(boolean enableShowCamera) {
        isEnableShowCamera = enableShowCamera;
    }

    public boolean isEnableAutoRecord() {
        return isEnableAutoRecord;
    }

    public void setEnableAutoRecord(boolean enableAutoRecord) {
        isEnableAutoRecord = enableAutoRecord;
    }

    public boolean isEnableShowTouches() {
        return isEnableShowTouches;
    }

    public void setEnableShowTouches(boolean enableShowTouches) {
        isEnableShowTouches = enableShowTouches;
    }

    public boolean isEnableCountdown() {
        return isEnableCountdown;
    }

    public void setEnableCountdown(boolean enableCountdown) {
        isEnableCountdown = enableCountdown;
    }

    public String getCountDownValue() {
        return countDownValue;
    }

    public void setCountDownValue(String countDownValue) {
        this.countDownValue = countDownValue;
    }

    @Override
    public String toString() {
        return "Config{" +
                "resolution='" + resolution + '\'' +
                ", frameRate='" + frameRate + '\'' +
                ", bitRate='" + bitRate + '\'' +
                ", orientation='" + orientation + '\'' +
                ", isEnableRecordAudio=" + isEnableRecordAudio +
                ", isEnableShowCamera=" + isEnableShowCamera +
                ", isEnableAutoRecord=" + isEnableAutoRecord +
                ", isEnableShowTouches=" + isEnableShowTouches +
                ", isEnableCountdown=" + isEnableCountdown +
                ", countDownValue='" + countDownValue + '\'' +
                '}';
    }
}
