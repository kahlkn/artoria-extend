package artoria.test.entity;

import java.io.Serializable;
import java.util.Date;

public class AbstractPerson implements Person, Serializable {
    private String name;
    private Integer gender;
    private Date birthday;
    private Integer age;
    private String wisdom;

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public Integer getGender() {

        return gender;
    }

    @Override
    public void setGender(Integer gender) {

        this.gender = gender;
    }

    @Override
    public Date getBirthday() {

        return birthday;
    }

    @Override
    public void setBirthday(Date birthday) {

        this.birthday = birthday;
    }

    @Override
    public Integer getAge() {

        return age;
    }

    @Override
    public void setAge(Integer age) {

        this.age = age;
    }

    @Override
    public String getWisdom() {

        return wisdom;
    }

    @Override
    public void setWisdom(String wisdom) {

        this.wisdom = wisdom;
    }

}
