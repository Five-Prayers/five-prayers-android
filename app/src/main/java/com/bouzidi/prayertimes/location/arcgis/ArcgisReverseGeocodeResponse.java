package com.bouzidi.prayertimes.location.arcgis;

public class ArcgisReverseGeocodeResponse {

    private ArcgisAddressResponse address;
    private ArcgisLocation location;

    public ArcgisAddressResponse getAddress() {
        return address;
    }

    public void setAddress(ArcgisAddressResponse address) {
        this.address = address;
    }

    public ArcgisLocation getLocation() {
        return location;
    }

    public void setLocation(ArcgisLocation location) {
        this.location = location;
    }
}