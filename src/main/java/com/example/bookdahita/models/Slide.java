package com.example.bookdahita.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_slides")
public class Slide {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sltitle")
    private String sltitle;

    @Column(name = "sllink")
    private String sllink;

    @Column(name = "slimage")
    private String slimage;

    @Column(name = "slactive")
    private Boolean slactive;

    @Column(name = "sltarget")
    private String sltarget;

    public Slide() {
    }

    public Slide(Long id, String sltitle, String sllink, String slimage, Boolean slactive, String sltarget) {
        this.id = id;
        this.sltitle = sltitle;
        this.sllink = sllink;
        this.slimage = slimage;
        this.slactive = slactive;
        this.sltarget = sltarget;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSltitle() {
        return sltitle;
    }

    public void setSltitle(String sltitle) {
        this.sltitle = sltitle;
    }

    public String getSllink() {
        return sllink;
    }

    public void setSllink(String sllink) {
        this.sllink = sllink;
    }

    public String getSlimage() {
        return slimage;
    }

    public void setSlimage(String slimage) {
        this.slimage = slimage;
    }

    public Boolean getSlactive() {
        return slactive;
    }

    public void setSlactive(Boolean slactive) {
        this.slactive = slactive;
    }

    public String getSltarget() {
        return sltarget;
    }

    public void setSltarget(String sltarget) {
        this.sltarget = sltarget;
    }
}
