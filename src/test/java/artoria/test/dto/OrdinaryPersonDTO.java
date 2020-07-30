package artoria.test.dto;

import java.util.Date;
import java.util.List;

public class OrdinaryPersonDTO {
    private String name;
    private Integer gender;
    private Date birthday;
    private Integer age;
    private String wisdom;
    private List<SkillDTO> skillList;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Integer getGender() {

        return gender;
    }

    public void setGender(Integer gender) {

        this.gender = gender;
    }

    public Date getBirthday() {

        return birthday;
    }

    public void setBirthday(Date birthday) {

        this.birthday = birthday;
    }

    public Integer getAge() {

        return age;
    }

    public void setAge(Integer age) {

        this.age = age;
    }

    public String getWisdom() {

        return wisdom;
    }

    public void setWisdom(String wisdom) {

        this.wisdom = wisdom;
    }

    public List<SkillDTO> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<SkillDTO> skillList) {

        this.skillList = skillList;
    }

}
