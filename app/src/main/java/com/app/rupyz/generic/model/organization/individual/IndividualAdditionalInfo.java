package com.app.rupyz.generic.model.organization.individual;

import java.util.List;

public class IndividualAdditionalInfo {
    private List<EmailInfo> email_info;
    private List<PhoneInfo> phone_info;
    private List<AddressInfo> address_info;
    private IdentityInfo identity_info;

    public List<EmailInfo> getEmail_info() {
        return email_info;
    }

    public void setEmail_info(List<EmailInfo> email_info) {
        this.email_info = email_info;
    }

    public List<PhoneInfo> getPhone_info() {
        return phone_info;
    }

    public void setPhone_info(List<PhoneInfo> phone_info) {
        this.phone_info = phone_info;
    }

    public List<AddressInfo> getAddress_info() {
        return address_info;
    }

    public void setAddress_info(List<AddressInfo> address_info) {
        this.address_info = address_info;
    }

    public IdentityInfo getIdentity_info() {
        return identity_info;
    }

    public void setIdentity_info(IdentityInfo identity_info) {
        this.identity_info = identity_info;
    }

    public PersonalInfo getPersonal_info() {
        return personal_info;
    }

    public void setPersonal_info(PersonalInfo personal_info) {
        this.personal_info = personal_info;
    }

    private PersonalInfo personal_info;
}
