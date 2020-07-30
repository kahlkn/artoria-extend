package artoria.user;

import artoria.data.AbstractExtraData;

import java.io.Serializable;

/**
 * User information.
 * @author Kahle
 */
public class UserInfo extends AbstractExtraData implements Serializable {
    private String id;
    private String username;
    private String displayName;
    private String nickname;
    private String realName;
    private String gender;
    private String avatar;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public String getNickname() {

        return nickname;
    }

    public void setNickname(String nickname) {

        this.nickname = nickname;
    }

    public String getRealName() {

        return realName;
    }

    public void setRealName(String realName) {

        this.realName = realName;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }

    public String getAvatar() {

        return avatar;
    }

    public void setAvatar(String avatar) {

        this.avatar = avatar;
    }

}
