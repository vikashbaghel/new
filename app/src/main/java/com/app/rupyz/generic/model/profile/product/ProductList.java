package com.app.rupyz.generic.model.profile.product;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.rupyz.databse.product.ProductListTypeConverters;
import com.app.rupyz.databse.staff.StringListTypeConverter;
import com.app.rupyz.model_kt.PackagingLevelModel;
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@TypeConverters({ProductListTypeConverters.class, StringListTypeConverter.class})
@Entity(tableName = "product_table")
public class ProductList {
    @SerializedName("id")
    @PrimaryKey()
    @Expose
    private Integer id;

    @SerializedName("unit")
    @Expose
    private String unit;

    @SerializedName("pics")
    @Expose
    private String pics;

    @SerializedName("mrp_unit")
    @Expose
    private String mrp_unit;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("variant_name")
    @Expose
    private String variantName;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("brand")
    @Expose
    private String brand;

    @SerializedName("mrp_price")
    @Expose
    private Double mrp_price;

    @SerializedName("gst")
    @Expose
    private Double gst;

    @SerializedName("product_url")
    @Expose
    private String productUrl;

    @SerializedName("display_pic_url")
    @Expose
    private String displayPicUrl;

    @SerializedName("nanoid")
    @Expose
    private String nanoid;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("min_price")
    @Expose
    private Integer minPrice;
    @SerializedName("max_price")
    @Expose
    private Integer maxPrice;
    @SerializedName("is_published")
    @Expose
    private Boolean isPublished;
    @SerializedName("is_out_of_stock")
    @Expose
    private Boolean isOutOfStock;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("packaging_unit")
    @Expose
    private String packaging_unit;

    @SerializedName("view_count")
    @Expose
    private Integer viewCount;

    private Integer addedVariantSize;
    @SerializedName("packaging_size")
    @Expose
    private Double packaging_size;

    @SerializedName("like_count")
    @Expose
    private Integer likeCount;

    @SerializedName("primary_product")
    @Expose
    private Integer primaryProduct;

    @SerializedName("gst_exclusive")
    @Expose
    private Boolean gst_exclusive;

    @SerializedName("telescope_pricing")
    @Expose
    private ArrayList<TelescopicPricingModel> telescopePricing;

    private PackagingLevelModel selectedPackagingLevel;

    @SerializedName("packaging_level")
    @Expose
    private ArrayList<PackagingLevelModel> packaging_level;

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Double getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(Double mrp_price) {
        this.mrp_price = mrp_price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getNanoid() {
        return nanoid;
    }

    public void setNanoid(String nanoid) {
        this.nanoid = nanoid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getGst_exclusive() {
        return gst_exclusive;
    }

    public void setGst_exclusive(Boolean gst_exclusive) {
        this.gst_exclusive = gst_exclusive;
    }

    private boolean isAddedToCart;

    private boolean enableUpdateQuantity = true;

    private Double qty;

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }


    public List<String> getPics_urls() {
        return pics_urls;
    }

    public void setPics_urls(List<String> pics_urls) {
        this.pics_urls = pics_urls;
    }

    @Ignore
    private List<String> pics_urls;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }


    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean getAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }


    public String getDisplayPicUrl() {
        return displayPicUrl;
    }

    public void setDisplayPicUrl(String displayPicUrl) {
        this.displayPicUrl = displayPicUrl;
    }


    public String getMrp_unit() {
        return mrp_unit;
    }

    public void setMrp_unit(String mrp_unit) {
        this.mrp_unit = mrp_unit;
    }

    public String getPackaging_unit() {
        return packaging_unit;
    }

    public void setPackaging_unit(String packaging_unit) {
        this.packaging_unit = packaging_unit;
    }

    public Double getPackaging_size() {
        return packaging_size;
    }

    public void setPackaging_size(Double packaging_size) {
        this.packaging_size = packaging_size;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public ArrayList<TelescopicPricingModel> getTelescopePricing() {
        return telescopePricing;
    }

    public void setTelescopePricing(ArrayList<TelescopicPricingModel> telescopePricing) {
        this.telescopePricing = telescopePricing;
    }

    public boolean isEnableUpdateQuantity() {
        return enableUpdateQuantity;
    }

    public void setEnableUpdateQuantity(boolean enableUpdateQuantity) {
        this.enableUpdateQuantity = enableUpdateQuantity;
    }

    public ArrayList<PackagingLevelModel> getPackaging_level() {
        return packaging_level;
    }

    public void setPackaging_level(ArrayList<PackagingLevelModel> packaging_level) {
        this.packaging_level = packaging_level;
    }

    public PackagingLevelModel getSelectedPackagingLevel() {
        return selectedPackagingLevel;
    }

    public void setSelectedPackagingLevel(PackagingLevelModel selectedPackagingLevel) {
        this.selectedPackagingLevel = selectedPackagingLevel;
    }

    public Boolean getOutOfStock() {
        return isOutOfStock;
    }

    public void setOutOfStock(Boolean outOfStock) {
        isOutOfStock = outOfStock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getPrimaryProduct() {
        return primaryProduct;
    }

    public void setPrimaryProduct(Integer primaryProduct) {
        this.primaryProduct = primaryProduct;
    }

    public Integer getAddedVariantSize() {
        return addedVariantSize;
    }

    public void setAddedVariantSize(Integer addedVariantSize) {
        this.addedVariantSize = addedVariantSize;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }
}