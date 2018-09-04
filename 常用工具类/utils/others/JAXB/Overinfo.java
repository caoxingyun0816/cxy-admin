package com.wondertek.mam.util.others.JAXB;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [一句话描述该类的功能]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @UpdateUser: [${user}]
 * @UpdateDate: [${date} ${time}]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0]
 */
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "overinfo")
@Entity
@Table(name = "OVERINFO")
public class Overinfo implements Serializable {
    private static final long serialVersionUID = 2579971237985854291L;
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @XmlTransient
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "UserinfoId")
    private Userinfo userinfo;

    @Column(name = "hobby", length = 20)
    private String hobby;

    @Temporal(TemporalType.DATE)
    @Column(name = "beginDate", length = 20)
    private Date beginDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "endDate", length = 20)
    private Date endDate;
    public String getHobby() {
        return hobby;
    }
    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
    public Date getBeginDate() {
        return beginDate;
    }
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Userinfo getUserinfo() {
        return userinfo;
    }
    public void setUserinfo(Userinfo userinfo) {
        this.userinfo = userinfo;
    }
}