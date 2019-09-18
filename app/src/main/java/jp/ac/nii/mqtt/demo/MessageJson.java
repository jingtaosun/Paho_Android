package jp.ac.nii.mqtt.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MessageJson {


    String sensor;
    String time;
    String  frame;

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public  String getFrame() {
        return frame;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getSensor() {
        return sensor;
    }

    public String timezone(String zone) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        System.out.println(sdf.format(new Date()));
        return sdf.format(new Date());
    }

}
