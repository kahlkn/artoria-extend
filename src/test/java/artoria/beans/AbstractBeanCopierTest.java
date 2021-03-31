package artoria.beans;

import artoria.convert.type.TypeConvertUtils;
import artoria.convert.type.TypeConverter;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.mock.MockUtils;
import artoria.test.dto.OrdinaryPersonDTO;
import artoria.test.dto.SimplePersonDTO;
import artoria.test.dto.SkillDTO;
import artoria.test.entity.Dog;
import artoria.test.entity.OrdinaryPerson;
import artoria.test.entity.Skill;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.ZERO;

abstract class AbstractBeanCopierTest {
    private static Logger log = LoggerFactory.getLogger(AbstractBeanCopierTest.class);
    private static final TypeConverter CONVERTER_AGENT = new TypeConverterAgent();

    void doCopyMapToObject(BeanCopier beanCopier) {
        Dog newDog = MockUtils.mock(Dog.class);
        Map<String, Object> fromMap = BeanUtils.beanToMap(newDog);
        Dog toDog = new Dog();
        log.info("From object: {}", JSON.toJSONString(fromMap));
        log.info("To object: {}", JSON.toJSONString(toDog));
        beanCopier.copy(fromMap, toDog, CONVERTER_AGENT);
        log.info("After the copy, From object: {}", JSON.toJSONString(fromMap));
        log.info("After the copy, To object: {}", JSON.toJSONString(toDog));
    }

    void doCopyObjectToMap(BeanCopier beanCopier) {
        Dog fromDog = MockUtils.mock(Dog.class);
        Map<String, Object> toMap = new HashMap<String, Object>();
        log.info("From object: {}", JSON.toJSONString(fromDog));
        log.info("To object: {}", JSON.toJSONString(toMap));
        beanCopier.copy(fromDog, toMap, CONVERTER_AGENT);
        log.info("After the copy, From object: {}", JSON.toJSONString(fromDog));
        log.info("After the copy, To object: {}", JSON.toJSONString(toMap));
    }

    void doCopyNullToValue(BeanCopier beanCopier) {
        Dog fromDog = MockUtils.mock(Dog.class);
        Dog toDog = new Dog();
        fromDog.setName(null);
        toDog.setName("ToDog's name");
        log.info("From object: {}", JSON.toJSONString(fromDog));
        log.info("To object: {}", JSON.toJSONString(toDog));
        beanCopier.copy(fromDog, toDog, CONVERTER_AGENT);
        log.info("After the copy, From object: {}", JSON.toJSONString(fromDog));
        log.info("After the copy, To object: {}", JSON.toJSONString(toDog));
        log.info("After the copy, The to object's name: {}", toDog.getName());
    }

    void doCopyObjToObjToTestTypeConvert(BeanCopier beanCopier) {
        SimplePersonDTO fromPerson = MockUtils.mock(SimplePersonDTO.class);
        fromPerson.setGender("1");
        OrdinaryPersonDTO toPerson = new OrdinaryPersonDTO();
        log.info("From object: {}", JSON.toJSONString(fromPerson));
        log.info("To object: {}", JSON.toJSONString(toPerson));
        try {
            beanCopier.copy(fromPerson, toPerson, new NoConverter());
            log.info("After the copy, From object: {}", JSON.toJSONString(fromPerson));
            log.info("After the copy, To object: {}", JSON.toJSONString(toPerson));
        }
        catch (Exception e) {
            log.info("Try copy obj to obj to test type convert error. ", e);
        }
    }

    void doCopyObjToOtherObjToTestPropertyList(BeanCopier beanCopier) {
        OrdinaryPerson fromPerson = MockUtils.mock(OrdinaryPerson.class);
        List<Skill> skillList = new ArrayList<Skill>();
        for (int i = ZERO; i < ONE; i++) {
            skillList.add(MockUtils.mock(Skill.class));
        }
        fromPerson.setSkillList(skillList);
        OrdinaryPersonDTO toPerson = new OrdinaryPersonDTO();
        log.info("From object: {}", JSON.toJSONString(fromPerson));
        log.info("To object: {}", JSON.toJSONString(toPerson));
        beanCopier.copy(fromPerson, toPerson, CONVERTER_AGENT);
        log.info("After the copy, From object: {}", JSON.toJSONString(fromPerson));
        log.info("After the copy, To object: {}", JSON.toJSONString(toPerson));
        try {
            List<SkillDTO> skillDTOList = toPerson.getSkillList();
            SkillDTO skillDTO = skillDTOList.get(ZERO);
            log.info("The list's bean: {}", skillDTO);
        }
        catch (Exception e) {
            log.info("Try get list's bean, It will throw an exception. ", e);
        }
    }

    static class NoConverter implements TypeConverter {

        @Override
        public Object convert(Object source, Class<?> target) {

            return source;
        }

    }

    static class TypeConverterAgent implements TypeConverter {

        @Override
        public Object convert(Object source, Class<?> target) {

            return TypeConvertUtils.convert(source, target);
        }

    }

}
