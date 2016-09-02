package models;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by trevorBye on 8/29/16.
 */
@Entity
@Table(name = "keyList", schema = "userManagement", catalog = "")
public class KeyListEntity {
    private String keyNumber;

    @Basic
    @Column(name = "keyNumber", nullable = true, length = 10)
    public String getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(String keyNumber) {
        this.keyNumber = keyNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyListEntity that = (KeyListEntity) o;

        if (keyNumber != null ? !keyNumber.equals(that.keyNumber) : that.keyNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return keyNumber != null ? keyNumber.hashCode() : 0;
    }

    private String id;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
