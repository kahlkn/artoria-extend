package artoria.test.entity;

import java.io.Serializable;
import java.util.List;

public class Menu implements Serializable {
    private String id;
    private String code;
    private String name;
    private String module;
    private String parentCode;
    private List<Menu> sonMenuList;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getModule() {

        return module;
    }

    public void setModule(String module) {

        this.module = module;
    }

    public String getParentCode() {

        return parentCode;
    }

    public void setParentCode(String parentCode) {

        this.parentCode = parentCode;
    }

    public List<Menu> getSonMenuList() {

        return sonMenuList;
    }

    public void setSonMenuList(List<Menu> sonMenuList) {

        this.sonMenuList = sonMenuList;
    }

}
