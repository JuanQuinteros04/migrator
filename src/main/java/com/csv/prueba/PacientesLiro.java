package com.csv.prueba;

import org.springframework.lang.Nullable;

import java.time.LocalDate;

public class PacientesLiro {

    private String name;
    private String sex;
    @Nullable
    private LocalDate birthDate;
    private boolean death;
    private String photo;

    public PacientesLiro(String name, String sex, LocalDate birthDate, boolean death, String photo) {
        this.name = name;
        this.sex = sex;
        this.birthDate = birthDate;
        this.death = death;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "PacientesLiro{" +
                "name=" + name  +
                ", sex=" + sex +
                ", birthDate=" + birthDate +
                ", death=" + death +
                ", photo='" + photo + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isDeath() {
        return death;
    }

    public void setDeath(boolean death) {
        this.death = death;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
