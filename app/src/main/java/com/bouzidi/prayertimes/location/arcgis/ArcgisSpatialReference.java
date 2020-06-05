package com.bouzidi.prayertimes.location.arcgis;

import com.google.gson.annotations.SerializedName;

class ArcgisSpatialReference {

    @SerializedName("wkid")
    private Integer wkid;

    @SerializedName("latestWkid")
    private Integer latestWkid;
}
