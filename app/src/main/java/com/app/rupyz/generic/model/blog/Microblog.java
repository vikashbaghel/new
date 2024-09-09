
package com.app.rupyz.generic.model.blog;

public class Microblog {

    private Integer id;
    private String title;
    private String subtitle;
    private String slug;
    private String feature_image_url;
    private String created_at;
    private String icon_image_url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getFeatureImageUrl() {
        return feature_image_url;
    }

    public void setFeatureImageUrl(String feature_image_url) {
        this.feature_image_url = feature_image_url;
    }

    public String getIconImageUrl() {
        return icon_image_url;
    }

    public void setIconImageUrl(String icon_image_url) {
        this.icon_image_url = icon_image_url;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

}
