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
import org.hibernate.validator.constraints.Length;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "USERINFO")
public class Userinfo implements Serializable{

    private static final long serialVersionUID = 7870351249722416047L;

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 50)
    @Length(max = 50)
    private String name;

    @Column(name = "ADDRESS", length = 50)
    @Length(max = 50)
    private String address;

    @Column(name = "JOB", length = 50)
    @Length(max = 50)
    private String job;

    @XmlElementWrapper(name = "overinfos")
    @OneToMany(cascade = CascadeType.ALL)
    @XmlElements(value = { @XmlElement(name = "overinfo", type = Overinfo.class) })
    private List<Overinfo> overinfos;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getJob() {
        return job;
    }
    public void setJob(String job) {
        this.job = job;
    }
    public List<Overinfo> getOverinfos() {
        return overinfos;
    }
    public void setOverinfos(List<Overinfo> overinfos) {
        this.overinfos = overinfos;
    }

}