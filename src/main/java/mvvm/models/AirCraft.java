package mvvm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AirCraft {
    @JsonProperty("id")
    private int id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("model")
    String model;
    @JsonProperty("totalCapacity")
    int totalCapacity;
    @JsonProperty("economyCapacity")
    int economyCapacity;
    @JsonProperty("businessCapacity")
    int businessCapacity;

    public AirCraft() {
    }

    public AirCraft(int id, String code, String model, int totalCapacity, int economyCapacity, int businessCapacity) {
        this.id = id;
        this.code = code;
        this.model = model;
        this.totalCapacity = totalCapacity;
        this.economyCapacity = economyCapacity;
        this.businessCapacity = businessCapacity;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getEconomyCapacity() {
        return economyCapacity;
    }

    public void setEconomyCapacity(int economyCapacity) {
        this.economyCapacity = economyCapacity;
    }

    public int getBusinessCapacity() {
        return businessCapacity;
    }

    public void setBusinessCapacity(int businessCapacity) {
        this.businessCapacity = businessCapacity;
    }
}