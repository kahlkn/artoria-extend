package artoria.user;

import artoria.common.Loader;

public interface UserLoader extends Loader {

    UserInfo load(Object input);

}
