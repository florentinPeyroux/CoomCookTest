package com.eservices.floamnx.coomcook.event.model;

public class Event {
    private String eventId;
    private String eventOwnerId;
    private String eventOwnerSurname;
    private String eventOwnerPicture;
    private String eventName;
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;
    private String eventCity;
    private String eventFood;
    private String eventInterest;
    private int maxNbrPersons;
    private int nbrParticipants;
    private String eventDescription;
    private String eventFoodUrl;
    private String participantID1;
    private String participantID2;
    private String participantID3;
    private String participantID4;
    private String participantName1;
    private String participantName2;
    private String participantName3;
    private String participantName4;
    private String participantPicture1;
    private String participantPicture2;
    private String participantPicture3;
    private String participantPicture4;

    public Event() {

    }


    public Event(String eventId, String eventOwnerId, String eventOwnerSurname, String eventOwnerPicture, String eventName, String eventDate,
                 String eventStartTime, String eventEndTime, String eventCity, String eventFood, String eventInterest,
                 int maxNbrPersons, String eventDescription, String eventFoodUrl) {
        this.eventId = eventId;
        this.eventOwnerId = eventOwnerId;
        this.eventOwnerSurname = eventOwnerSurname;
        this.eventOwnerPicture = eventOwnerPicture;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventCity = eventCity;
        this.eventFood = eventFood;
        this.eventInterest = eventInterest;
        this.maxNbrPersons = maxNbrPersons;
        this.nbrParticipants = 0;
        this.eventDescription = eventDescription;
        this.eventFoodUrl = eventFoodUrl;
        this.participantID1 = "vide";
        this.participantID2 = "vide";
        this.participantID3 = "vide";
        this.participantID4 = "vide";
        this.participantName1 = "vide";
        this.participantName2 = "vide";
        this.participantName3 = "vide";
        this.participantName4 = "vide";
        this.participantPicture1 = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png";
        this.participantPicture2 = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png";
        this.participantPicture3 = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png";
        this.participantPicture4 = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png";
    }

    public Event(String eventId, String eventOwnerId, String eventName, String eventDate,
                 String eventStartTime, String eventEndTime, String eventCity, String eventFood, String eventInterest,
                 int maxNbrPersons, String eventDescription, String eventFoodUrl,
                 String eventOwnerSurname, String eventOwnerPicture, int nbrParticipants, String participant1Name, String participant2Name, String participant3Name,
                 String participant4Name, String participantID1, String participantID2, String participantID3, String participantID4,
                 String paritipantPic1, String paritipantPic2, String paritipantPic3, String paritipantPic4) {
        this.eventId = eventId;
        this.eventOwnerId = eventOwnerId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventCity = eventCity;
        this.eventFood = eventFood;
        this.eventInterest = eventInterest;
        this.maxNbrPersons = maxNbrPersons;
        this.eventDescription = eventDescription;
        this.eventFoodUrl = eventFoodUrl;

        this.eventOwnerSurname = eventOwnerSurname;
        this.eventOwnerPicture = eventOwnerPicture;
        this.nbrParticipants = nbrParticipants;
        this.participantID1 = participantID1;
        this.participantID2 = participantID2;
        this.participantID3 = participantID3;
        this.participantID4 = participantID4;
        this.participantName1 = participant1Name;
        this.participantName2 = participant2Name;
        this.participantName3 = participant3Name;
        this.participantName4 = participant4Name;
        this.participantPicture1 = paritipantPic1;
        this.participantPicture2 = paritipantPic2;
        this.participantPicture3 = paritipantPic3;
        this.participantPicture4 = paritipantPic4;

    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventCity() {
        return eventCity;
    }

    public String getEventFood() {
        return eventFood;
    }

    public String getEventInterest() {
        return eventInterest;
    }

    public void setEventInterest(String eventInterest) {
        this.eventInterest = eventInterest;
    }

    public int getMaxNbrPersons() {
        return maxNbrPersons;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventOwnerId() {
        return eventOwnerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventFoodUrl() {
        return eventFoodUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public String getParticipantName1() {
        return participantName1;
    }

    public void setParticipantName1(String participantName1) {
        this.participantName1 = participantName1;
    }

    public String getParticipantName2() {
        return participantName2;
    }

    public void setParticipantName2(String participantName2) {
        this.participantName2 = participantName2;
    }

    public String getParticipantName3() {
        return participantName3;
    }

    public void setParticipantName3(String participantName3) {
        this.participantName3 = participantName3;
    }

    public String getParticipantName4() {
        return participantName4;
    }

    public void setParticipantName4(String participantName4) {
        this.participantName4 = participantName4;
    }

    public int getNbrParticipants() {
        return nbrParticipants;
    }

    public void setNbrParticipants(int nbrParticipants) {
        this.nbrParticipants = nbrParticipants;
    }

    public String getParticipantPicture1() {
        return participantPicture1;
    }

    public void setParticipantPicture1(String participantPicture1) {
        this.participantPicture1 = participantPicture1;
    }

    public String getParticipantPicture2() {
        return participantPicture2;
    }

    public void setParticipantPicture2(String participantPicture2) {
        this.participantPicture2 = participantPicture2;
    }

    public String getParticipantPicture3() {
        return participantPicture3;
    }

    public void setParticipantPicture3(String participantPicture3) {
        this.participantPicture3 = participantPicture3;
    }

    public String getParticipantPicture4() {
        return participantPicture4;
    }

    public void setParticipantPicture4(String participantPicture4) {
        this.participantPicture4 = participantPicture4;
    }

    public String getParticipantID1() {
        return participantID1;
    }

    public void setParticipantID1(String participantID1) {
        this.participantID1 = participantID1;
    }

    public String getParticipantID2() {
        return participantID2;
    }

    public void setParticipantID2(String participantID2) {
        this.participantID2 = participantID2;
    }

    public String getParticipantID3() {
        return participantID3;
    }

    public void setParticipantID3(String participantID3) {
        this.participantID3 = participantID3;
    }

    public String getParticipantID4() {
        return participantID4;
    }

    public void setParticipantID4(String participantID4) {
        this.participantID4 = participantID4;
    }

    public String getEventOwnerSurname() {
        return eventOwnerSurname;
    }

    public String getEventOwnerPicture() {
        return eventOwnerPicture;
    }

}

