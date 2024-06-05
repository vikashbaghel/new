package com.app.rupyz.ui.organization.onboarding.model;

public class LanguageListData {
    private String languageName, languageSymbol;
    private int languageImage;
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public LanguageListData(String languageName, String languageSymbol) {
        this.languageName = languageName;
        this.languageSymbol = languageSymbol;
    }

    public String getLanguageSymbol() {
        return languageSymbol;
    }

    public void setLanguageSymbol(String languageSymbol) {
        this.languageSymbol = languageSymbol;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int getLanguageImage() {
        return languageImage;
    }

    public void setLanguageImage(int languageImage) {
        this.languageImage = languageImage;
    }
}
