package com.example.henriwilander.viikko9;

public class Cinema {

    private int cinemaId;
    private String cinemaName;

    public Cinema(int id, String name) {
        cinemaId = id;
        cinemaName = name;
    }

    public int getId() {

        return cinemaId;
    }

    public String getName() {
        return cinemaName;
    }


}
