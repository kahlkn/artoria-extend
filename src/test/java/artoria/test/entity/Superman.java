package artoria.test.entity;

import java.util.List;

public class Superman extends AbstractPerson {
    private List<SpecialSkill> skillList;

    public List<SpecialSkill> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<SpecialSkill> skillList) {

        this.skillList = skillList;
    }

}
