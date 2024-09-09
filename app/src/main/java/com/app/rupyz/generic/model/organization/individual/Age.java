package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class Age {
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @SerializedName("Age")
    private String age;
}
