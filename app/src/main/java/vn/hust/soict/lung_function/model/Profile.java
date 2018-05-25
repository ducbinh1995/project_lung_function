package vn.hust.soict.lung_function.model;

/**
 * Created by tulc on 15/03/2017.
 */
public class Profile {
    public static final String REGION_NORTHEN = "B";
    public static final String REGION_CENTRAL = "T";
    public static final String REGION_SOUTH = "N";

    private String userId;
    private String email;
    private String password;
    private String name;
    private String isMale;
    private String birthDay;
    private String weight;
    private String height;
    private String region;
    private String isSmoking;
    private String isCareTakers;

    public Profile() {

    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; };

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String isMale() {
        return isMale;
    }

    public void setMale(String male) {
        isMale = male;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String isSmoking() {
        return isSmoking;
    }

    public void setSmoking(String smoking) {
        isSmoking = smoking;
    }

    public String isCareTakers() { return isCareTakers; }

    public void setCareTakers(String careTakers) {
        isCareTakers = careTakers;
    }
}
