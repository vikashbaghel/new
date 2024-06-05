package com.app.rupyz.generic.model.product;

import com.app.rupyz.model_kt.PackagingLevelModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailInfoModel implements Serializable {
    private int id;
    private List<String> pics_urls;
    private String category;
    private int min_price, max_price;
    private List<Integer> pics;
    private String description, mrp_unit, avaliable_stock, unit, name, code, nanoid, packaging_unit, hsn_code, brand;
    private Boolean is_published, gst_exclusive, is_out_of_stock;
    private int view_count, like_count, organization, created_by;
    private Integer display_pic;
    private Double gst, packaging_size;
    private double price, mrp_price;
    private List<PicMapModel> pics_map;
    private ArrayList<PackagingLevelModel> packaging_level;
    private PicMapModel display_pic_map;

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    private String product_url;

    public HashMap<String, String> getSpecification() {
        return specification;
    }

    public void setSpecification(HashMap<String, String> specification) {
        this.specification = specification;
    }

    private HashMap<String, String> specification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPics_urls() {
        return pics_urls;
    }

    public void setPics_urls(List<String> pics_urls) {
        this.pics_urls = pics_urls;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNanoid() {
        return nanoid;
    }

    public void setNanoid(String nanoid) {
        this.nanoid = nanoid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public int getMax_price() {
        return max_price;
    }

    public void setMax_price(int max_price) {
        this.max_price = max_price;
    }

    public List<Integer> getPics() {
        return pics;
    }

    public void setPics(List<Integer> pics) {
        this.pics = pics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAvaliable_stock() {
        return avaliable_stock;
    }

    public void setAvaliable_stock(String avaliable_stock) {
        this.avaliable_stock = avaliable_stock;
    }

    public Boolean getIs_published() {
        return is_published;
    }

    public void setIs_published(Boolean is_published) {
        this.is_published = is_published;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(double mrp_price) {
        this.mrp_price = mrp_price;
    }

    public Boolean getGst_exclusive() {
        return gst_exclusive;
    }

    public void setGst_exclusive(Boolean gst_exclusive) {
        this.gst_exclusive = gst_exclusive;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Integer getDisplay_pic() {
        return display_pic;
    }

    public void setDisplay_pic(Integer display_pic) {
        this.display_pic = display_pic;
    }

    public List<PicMapModel> getPics_map() {
        return pics_map;
    }

    public void setPics_map(List<PicMapModel> pics_map) {
        this.pics_map = pics_map;
    }

    public PicMapModel getDisplay_pic_map() {
        return display_pic_map;
    }

    public void setDisplay_pic_map(PicMapModel display_pic_map) {
        this.display_pic_map = display_pic_map;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPackaging_size() {
        return packaging_size;
    }

    public void setPackaging_size(Double packaging_size) {
        this.packaging_size = packaging_size;
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


    public String getHsn_code() {
        return hsn_code;
    }

    public void setHsn_code(String hsn_code) {
        this.hsn_code = hsn_code;
    }

    public ArrayList<PackagingLevelModel> getPackaging_level() {
        return packaging_level;
    }

    public void setPackaging_level(ArrayList<PackagingLevelModel> packaging_level) {
        this.packaging_level = packaging_level;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getIs_out_of_stock() {
        return is_out_of_stock;
    }

    public void setIs_out_of_stock(Boolean is_out_of_stock) {
        this.is_out_of_stock = is_out_of_stock;
    }
}
