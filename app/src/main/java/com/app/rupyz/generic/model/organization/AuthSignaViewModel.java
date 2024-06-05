package com.app.rupyz.generic.model.organization;

import java.util.List;

public class AuthSignaViewModel {
    public List<String> getAuthorized_signatories() {
        return authorized_signatories;
    }

    public void setAuthorized_signatories(List<String> authorized_signatories) {
        this.authorized_signatories = authorized_signatories;
    }

    private List<String> authorized_signatories;

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    private int org_id;
}
