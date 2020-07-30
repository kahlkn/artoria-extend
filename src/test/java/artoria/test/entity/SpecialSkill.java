package artoria.test.entity;

public class SpecialSkill extends Skill {
    private String acquisitionCondition;
    private Integer usageRate;

    public String getAcquisitionCondition() {

        return acquisitionCondition;
    }

    public void setAcquisitionCondition(String acquisitionCondition) {

        this.acquisitionCondition = acquisitionCondition;
    }

    public Integer getUsageRate() {

        return usageRate;
    }

    public void setUsageRate(Integer usageRate) {

        this.usageRate = usageRate;
    }

}
