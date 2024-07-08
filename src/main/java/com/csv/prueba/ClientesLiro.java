package com.csv.prueba;

import net.iryndin.jdbf.core.DbfRecord;

public class ClientesLiro {

    private String areaPhoneNumber;
    private String name;
    private String email;

    private Integer codigo;

    public ClientesLiro(String areaPhoneNumber, String name, String email, Integer codigo) {
        this.areaPhoneNumber = areaPhoneNumber;
        this.name = name;
        this.email = email;
        this.codigo = codigo;
    }


    public String getAreaPhoneNumber() {
        return areaPhoneNumber;
    }

    public void setAreaPhoneNumber(String areaPhoneNumber) {
        this.areaPhoneNumber = areaPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "ClientesLiro{" +
                "areaPhoneNumber='" + areaPhoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", codigo=" + codigo +
                '}';
    }
}
