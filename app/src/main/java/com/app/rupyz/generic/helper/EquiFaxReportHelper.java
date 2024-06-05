package com.app.rupyz.generic.helper;

import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.HomeDataInfo;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.experian.ExperianEMIResponse;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EquiFaxReportHelper {
    private String commercialReport;
    private String retailReport;
    private String experianReport;
    private HomeDataInfo homeDataInfo;
    private OrgProfileInfoModel orgProfileInfoModel;
    private ExperianEMIResponse experianEMI;
    private CreateEMIResponse createEMIResponse;
    private CreateEMIResponse createIndividualEMIResponse;
    private int orgId = 0;
    private String gstId;
    private static final EquiFaxReportHelper instance = new EquiFaxReportHelper();

    public static EquiFaxReportHelper getInstance() {
        return instance;
    }

    private EquiFaxReportHelper() {
    }

    public void setEquiFaxCommercial(String commercialReport) {
        this.commercialReport = commercialReport;

    }

    public void setEquiFaxRetail(String retailReport) {
        this.retailReport = retailReport;
    }

    public void setExperianReport(String experianReport) {
        this.experianReport = experianReport;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getOrgId() {
        Logger.errorLogger(this.getClass().getName(), orgId + "");
        return orgId;
    }

    public void setGstId(String gstId) {
        this.gstId = gstId;
    }

    public String getGstId() {
        Logger.errorLogger(this.getClass().getName(), gstId);
        return gstId;
    }

    public EquiFaxInfoModel getCommercialReport() {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(this.commercialReport);
            Gson gson = new Gson();
            return gson.fromJson(jsonObj.get("data"), EquiFaxInfoModel.class);
        } catch (Exception exception) {
            return new EquiFaxInfoModel();
        }
    }

    public EquiFaxIndividualInfoModel getRetailReport() {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(this.retailReport);
            Gson gson = new Gson();
            return gson.fromJson(jsonObj.get("data"), EquiFaxIndividualInfoModel.class);
        } catch (Exception ex) {
            Logger.errorLogger("Helloooooo", ex.getMessage() + "");
            return null;
        }
    }

    public ExperianInfoModel getExperianReport() {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(this.experianReport);
            Gson gson = new Gson();
            return gson.fromJson(jsonObj.get("data"), ExperianInfoModel.class);
        } catch (Exception ex) {
            Logger.errorLogger("Helloooooo", ex.getMessage() + "");
            return null;
        }
    }

    public void setExperianEMI(ExperianEMIResponse experianEMI) {
        this.experianEMI = experianEMI;
    }

    public ExperianEMIResponse getExperianEMI() {
        try {
            return experianEMI;
        } catch (Exception ex) {
            Logger.errorLogger("Helloooooo", ex.getMessage() + "");
            return null;
        }
    }


    public void setEquifaxCommercialEMI(CreateEMIResponse experianEMI) {
        this.createEMIResponse = experianEMI;
    }

    public CreateEMIResponse getEquifaxCommercialEMI() {
        try {
            return createEMIResponse;
        } catch (Exception ex) {
            Logger.errorLogger("Helloooooo", ex.getMessage() + "");
            return null;
        }
    }

    public void setEquifaxIndividualEMI(CreateEMIResponse experianEMI) {
        this.createIndividualEMIResponse = experianEMI;
    }

    public CreateEMIResponse getEquifaxIndividualEMI() {
        try {
            return createIndividualEMIResponse;
        } catch (Exception ex) {
            Logger.errorLogger("Helloooooo", ex.getMessage() + "");
            return null;
        }
    }

    //Save Compliance

    public void setCompliance(HomeDataInfo homeDataInfo) {
        this.homeDataInfo = homeDataInfo;
    }

    public HomeDataInfo getHomeDataInfo(){
        try {
            return homeDataInfo;
        } catch (Exception e){
            Logger.errorLogger("Home Data", e.getMessage()+"");
            return null;
        }
    }

    //Save Organization Info
    public void setOrgProfile(OrgProfileInfoModel orgProfileInfoModel) {
        this.orgProfileInfoModel = orgProfileInfoModel;
    }

    public OrgProfileInfoModel getOrgProfile(){
        try {
            return orgProfileInfoModel;
        } catch (Exception e){
            Logger.errorLogger("Org DATA", e.getMessage()+"");
            return null;
        }
    }
}
