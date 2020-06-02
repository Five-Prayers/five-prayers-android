package com.bouzidi.prayer_times.location.arcgis;

public class ArcgisLocation {

    private Double x;
    private Double y;
    private ArcgisSpatialReference spatialReference;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public ArcgisSpatialReference getSpatialReference() {
        return spatialReference;
    }

    public void setSpatialReference(ArcgisSpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }
}
