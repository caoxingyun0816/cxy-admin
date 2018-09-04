package com.imooc.cache;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.UniqueConstraint;
/**
 *
 * SystemConfig
 *
 * @version 1.0.0
 */

@Entity
@Table(name = "m_system_config", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"config_key"})
})
public class SystemConfig  {

    private static final long serialVersionUID = -3457185596618347969L;

    private Long id; // id
    private String configKey; // 系统参数主键
    private String configValue; // 系统参数值
    private String detail; // 系统参数说明

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "m_system_config_SEQ")
    @SequenceGenerator(name = "m_system_config_SEQ", allocationSize = 1, sequenceName = "m_system_config_SEQ")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "config_key", length=50, nullable = false)
    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    @Column(name = "config_value", length=200, nullable = false)
    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }


    @Column(name = "detail", length=500, nullable = true)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((configKey == null) ? 0 : configKey.hashCode());
        result = prime * result
                + ((configValue == null) ? 0 : configValue.hashCode());
        result = prime * result + ((detail == null) ? 0 : detail.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SystemConfig other = (SystemConfig) obj;
        if (configKey == null) {
            if (other.configKey != null)
                return false;
        } else if (!configKey.equals(other.configKey))
            return false;
        if (configValue == null) {
            if (other.configValue != null)
                return false;
        } else if (!configValue.equals(other.configValue))
            return false;
        if (detail == null) {
            if (other.detail != null)
                return false;
        } else if (!detail.equals(other.detail))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "SystemConfig [id=" + id + ", configKey=" + configKey
                + ", configValue=" + configValue + ", detail=" + detail + "]";
    }


}
