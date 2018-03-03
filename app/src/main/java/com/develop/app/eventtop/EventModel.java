package com.develop.app.eventtop;

public class EventModel {
    private int id;
    private String name;
    private String username;
    private float price;
    private String location;
    private int cityId;
    private String avatar;
    private String coverAvatar;
    private int createdBy;
    private int hosted_by;
    private String description;
    private String sponsers;
    private String registerLink;
    private int views;
    private String sliderType;
    private int sliderOrder;
    private int isHidden;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String createdAt;
    private String updatedAt;

    public EventModel(int id, String name, String username, float price, String location, int cityId, String avatar, String coverAvatar, int createdBy, int hosted_by, String description, String sponsers, String registerLink, int views, String sliderType, int sliderOrder, int isHidden, String startDate, String endDate, String startTime, String endTime, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.price = price;
        this.location = location;
        this.cityId = cityId;
        this.avatar = avatar;
        this.coverAvatar = coverAvatar;
        this.createdBy = createdBy;
        this.hosted_by = hosted_by;
        this.description = description;
        this.sponsers = sponsers;
        this.registerLink = registerLink;
        this.views = views;
        this.sliderType = sliderType;
        this.sliderOrder = sliderOrder;
        this.isHidden = isHidden;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public EventModel(String avatar) {
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public float getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public int getCityId() {
        return cityId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCoverAvatar() {
        return coverAvatar;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public int getHosted_by() {
        return hosted_by;
    }

    public String getDescription() {
        return description;
    }

    public String getSponsers() {
        return sponsers;
    }

    public String getRegisterLink() {
        return registerLink;
    }

    public int getViews() {
        return views;
    }

    public String getSliderType() {
        return sliderType;
    }

    public int getSliderOrder() {
        return sliderOrder;
    }

    public int isHidden() {
        return isHidden;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
