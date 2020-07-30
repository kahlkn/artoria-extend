package artoria.data;

/**
 * Used to represent specific applications.
 * @see <a href="https://en.wikipedia.org/wiki/Application_software">Application software</a>
 * @author Kahle
 */
public class App extends AbstractExtraData {
    /**
     * A unique identifier for a specific application.
     */
    private String id;
    /**
     * Name of the application.
     */
    private String name;
    /**
     * Description of the application.
     */
    private String description;
    /**
     * Type of the application, such as "web", "app".
     * This field is not included in the "id".
     * And in some cases is null.
     */
    private String type;
    /**
     * Version of the application.
     * This field is not included in the "id".
     * And in some cases is null.
     */
    private String version;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

}
