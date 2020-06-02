package com.bouzidi.prayer_times.location.arcgis;

import com.google.gson.annotations.SerializedName;

public class ArcgisAddressResponse {

    @SerializedName("Match_addr")
    private String matchAddr;

    @SerializedName("LongLabel")
    private String longLabel;

    @SerializedName("ShortLabel")
    private String shortLabel;

    @SerializedName("Addr_type")
    private String addr_type;

    @SerializedName("Type")
    private String Type;

    @SerializedName("PlaceName")
    private String placeName;

    @SerializedName("AddNum")
    private String addNum;

    @SerializedName("Address")
    private String address;

    @SerializedName("Block")
    private String block;

    @SerializedName("Sector")
    private String sector;

    @SerializedName("Neighborhood")
    private String neighborhood;

    @SerializedName("District")
    private String district;

    @SerializedName("City")
    private String city;

    @SerializedName("MetroArea")
    private String metroArea;

    @SerializedName("Subregion")
    private String subregion;

    @SerializedName("Region")
    private String region;

    @SerializedName("Territory")
    private String territory;

    @SerializedName("Postal")
    private String postal;

    @SerializedName("PostalExt")
    private String postalExt;

    @SerializedName("CountryCode")
    private String countryCode;

    public String getMatchAddr() {
        return matchAddr;
    }

    public void setMatchAddr(String matchAddr) {
        this.matchAddr = matchAddr;
    }

    public String getLongLabel() {
        return longLabel;
    }

    public void setLongLabel(String longLabel) {
        this.longLabel = longLabel;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public String getAddr_type() {
        return addr_type;
    }

    public void setAddr_type(String addr_type) {
        this.addr_type = addr_type;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddNum() {
        return addNum;
    }

    public void setAddNum(String addNum) {
        this.addNum = addNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMetroArea() {
        return metroArea;
    }

    public void setMetroArea(String metroArea) {
        this.metroArea = metroArea;
    }

    public String getSubregion() {
        return subregion;
    }

    public void setSubregion(String subregion) {
        this.subregion = subregion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getPostalExt() {
        return postalExt;
    }

    public void setPostalExt(String postalExt) {
        this.postalExt = postalExt;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
