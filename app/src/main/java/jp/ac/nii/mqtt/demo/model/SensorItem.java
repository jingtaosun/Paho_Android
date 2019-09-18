package jp.ac.nii.mqtt.demo.model;

import java.io.Serializable;

public class SensorItem implements Serializable {

    private String sensorName;
    private String sensorTopic;
    private String sensorType;

    private int qos;
    private boolean retained;

    public SensorItem(){
        sensorName = "";
    }

    public SensorItem(String sensorName,String sensorTopic, String sensorType, int qos, boolean retained) {
        this.sensorName = sensorName;
        this.sensorTopic = sensorTopic;
        this.sensorType = sensorType;
        this.qos = qos;
        this.retained = retained;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorTopic() {
        return sensorTopic;
    }

    public void setSensorTopic(String sensorTopic) {
        this.sensorTopic = sensorTopic;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }
    public boolean getRetained() {
        return retained;
    }

    @Override
    public String toString() {
        return "SensorItem{" +
                "sensorName=" + sensorName +
                ", sensorTopic=" + sensorTopic +
                ", sensorType=" + sensorType +
                ", qos=" + qos +
                ", retained=" + retained +
                '}';
    }


}
