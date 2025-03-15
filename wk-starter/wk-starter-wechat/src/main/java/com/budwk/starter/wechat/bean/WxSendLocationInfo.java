package com.budwk.starter.wechat.bean;

public class WxSendLocationInfo {

    private double location_X;
    private double location_Y;
    private double scale;
    private String label;
    private String poiname;
    public double getLocation_X() {
        return location_X;
    }
    public void setLocation_X(double location_X) {
        this.location_X = location_X;
    }
    public double getLocation_Y() {
        return location_Y;
    }
    public void setLocation_Y(double location_Y) {
        this.location_Y = location_Y;
    }
    public double getScale() {
        return scale;
    }
    public void setScale(double scale) {
        this.scale = scale;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getPoiname() {
        return poiname;
    }
    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }
}
