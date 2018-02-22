package com.eservices.floamnx.coomcook.authentification;

public class User {
    private String userID;
    private String surname;
    private String name;
    private String age;
    private String birthDate;
    private String plate;
    private String interest;
    private String city;
    private String imageURL;
    private int numStar;
    private int numVoters;


    public int getNumStar() {
        return numStar;
    }

    public void setNumStar(int numStar) {
        this.numStar = numStar;
    }

    public int getNumVoters() {
        return numVoters;
    }

    public void setNumVoters(int numVoters) {
        this.numVoters = numVoters;
    }

    public User(String userID, String surname, String name, String birthDate, String age, String plate, String interest, String city, String imageURL) {
        this.userID = userID;
        this.surname = surname;
        this.name = name;
        this.birthDate = birthDate;
        this.plate = plate;
        this.interest = interest;
        this.city = city;
        this.age = age;
        this.imageURL=imageURL;
        this.numStar=3;
        this.numVoters=0;
    }

    public User(String userID, String surname, String name, String birthDate, String age, String plate, String interest, String city, String imageURL, int numStar, int numVoters) {
        this.userID = userID;
        this.surname = surname;
        this.name = name;
        this.birthDate = birthDate;
        this.plate = plate;
        this.interest = interest;
        this.city = city;
        this.age = age;
        this.imageURL=imageURL;
        this.numStar=numStar;
        this.numVoters=numVoters;
    }



    public User() {
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPlate() {
        return plate;
    }

    public String getInterest() {
        return interest;
    }

    public String getCity() {
        return city;
    }

    public String getImageURL() {
        return imageURL;
    }


}
