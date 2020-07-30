package artoria.data;

import artoria.common.Loader;

public interface DataLoader extends Loader {

    Object load(Object... args);

}
