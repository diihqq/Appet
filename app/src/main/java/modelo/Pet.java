package modelo;

import modelo.enums.Gait;
import modelo.enums.Gender;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Pet {
    private int PetId;
    private User user;
    private String name;
    private Breed breed;
    private Gender gender;
    private String furColor;
    private Gait gait;
    private int age;
    private String particulars;
    private String qrCode;
    private Boolean lost;

    public Pet(int petId, User user, String name, Breed breed, Gender gender, String furColor, Gait gait, int age, String particulars, String qrCode, Boolean lost) {
        PetId = petId;
        this.user = user;
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.furColor = furColor;
        this.gait = gait;
        this.age = age;
        this.particulars = particulars;
        this.qrCode = qrCode;
        this.lost = lost;
    }

    public int getPetId() {
        return PetId;
    }

    public void setPetId(int petId) {
        PetId = petId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFurColor() {
        return furColor;
    }

    public void setFurColor(String furColor) {
        this.furColor = furColor;
    }

    public Gait getGait() {
        return gait;
    }

    public void setGait(Gait gait) {
        this.gait = gait;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Boolean getLost() {
        return lost;
    }

    public void setLost(Boolean lost) {
        this.lost = lost;
    }
}
