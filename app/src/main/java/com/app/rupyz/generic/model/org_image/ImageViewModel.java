//package com.app.rupyz.generic.model.org_image;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//public class ImageViewModel implements Parcelable {
//    private int id;
//    private int organization;
//    private String image_url;
//
//    protected ImageViewModel(Parcel in) {
//        id = in.readInt();
//        organization = in.readInt();
//        image_url = in.readString();
//        created_by = in.readInt();
//    }
//
//    public static final Creator<ImageViewModel> CREATOR = new Creator<ImageViewModel>() {
//        @Override
//        public ImageViewModel createFromParcel(Parcel in) {
//            return new ImageViewModel(in);
//        }
//
//        @Override
//        public ImageViewModel[] newArray(int size) {
//            return new ImageViewModel[size];
//        }
//    };
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getOrganization() {
//        return organization;
//    }
//
//    public void setOrganization(int organization) {
//        this.organization = organization;
//    }
//
//    public String getImage_url() {
//        return image_url;
//    }
//
//    public void setImage_url(String image_url) {
//        this.image_url = image_url;
//    }
//
//    public int getCreated_by() {
//        return created_by;
//    }
//
//    public void setCreated_by(int created_by) {
//        this.created_by = created_by;
//    }
//
//    private int created_by;
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeInt(organization);
//        dest.writeString(image_url);
//        dest.writeInt(created_by);
//    }
//}
