package artoria.test.entity;

import java.util.List;

public class OrdinaryPerson extends AbstractPerson {
    private List<Skill> skillList;

    public List<Skill> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {

        this.skillList = skillList;
    }

}
