package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

public class CurrentApplicantAddressDetails {
    @SerializedName("City")
    public String city;
    @SerializedName("State")
    public String state;
    @SerializedName("PINCode")
    public String pINCode;
    @SerializedName("Landmark")
    public Object landmark;
    @SerializedName("Country_Code")
    public String country_Code;
    @SerializedName("BldgNoSocietyName")
    public Object bldgNoSocietyName;
    @SerializedName("FlatNoPlotNoHouseNo")
    public String flatNoPlotNoHouseNo;
    @SerializedName("RoadNoNameAreaLocality")
    public Object roadNoNameAreaLocality;
}
