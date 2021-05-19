package com.example.armeasure;

public class Measurement {

    private String ObjectName;
    private String dimension;
    private float measurement;
    private String unit;

    public String getObjectName(){
        return ObjectName;
    }

    public String getDimension(){
        return dimension;
    }

    public String getUnit(){
        return unit;
    }

    public float getMeasurement(){
        return measurement;
    }

    public void setObjectName(String ObjectName){
        this.ObjectName = ObjectName;
    }

    public void setDimension(String dimension){
        this.dimension = dimension;
    }

    public void setMeasurement(float measurement) {
        this.measurement = measurement;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
