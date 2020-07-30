package artoria.data;

import java.io.Serializable;

/**
 * WebBrowser.
 * @see <a href="https://en.wikipedia.org/wiki/Web_browser">Web browser</a>
 * @author Kahle
 */
public class WebBrowser extends AbstractExtraData implements Serializable {
    private String name;
    private String type;
    private String version;
    private String codeName;
    private String bits;
    private String mode;
    private String manufacturer;
    private String cssVersion;
    private Boolean alpha;
    private Boolean beta;
    private Boolean syndicationReader;
    private String description;
    private RenderingEngine renderingEngine;
    private JavaScriptEngine javaScriptEngine;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
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

    public String getCodeName() {

        return codeName;
    }

    public void setCodeName(String codeName) {

        this.codeName = codeName;
    }

    public String getBits() {

        return bits;
    }

    public void setBits(String bits) {

        this.bits = bits;
    }

    public String getMode() {

        return mode;
    }

    public void setMode(String mode) {

        this.mode = mode;
    }

    public String getManufacturer() {

        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {

        this.manufacturer = manufacturer;
    }

    public String getCssVersion() {

        return cssVersion;
    }

    public void setCssVersion(String cssVersion) {

        this.cssVersion = cssVersion;
    }

    public Boolean getAlpha() {

        return alpha;
    }

    public void setAlpha(Boolean alpha) {

        this.alpha = alpha;
    }

    public Boolean getBeta() {

        return beta;
    }

    public void setBeta(Boolean beta) {

        this.beta = beta;
    }

    public Boolean getSyndicationReader() {

        return syndicationReader;
    }

    public void setSyndicationReader(Boolean syndicationReader) {

        this.syndicationReader = syndicationReader;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public RenderingEngine getRenderingEngine() {

        return renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {

        this.renderingEngine = renderingEngine;
    }

    public JavaScriptEngine getJavaScriptEngine() {

        return javaScriptEngine;
    }

    public void setJavaScriptEngine(JavaScriptEngine javaScriptEngine) {

        this.javaScriptEngine = javaScriptEngine;
    }

    /**
     * WebBrowser RenderingEngine.
     * @see <a href="https://en.wikipedia.org/wiki/Browser_engine">Browser engine</a>
     * @author Kahle
     */
    public static class RenderingEngine extends AbstractExtraData implements Serializable {
        private String name;
        private String version;
        private String codeName;
        private String manufacturer;
        private String description;

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }

        public String getVersion() {

            return version;
        }

        public void setVersion(String version) {

            this.version = version;
        }

        public String getCodeName() {

            return codeName;
        }

        public void setCodeName(String codeName) {

            this.codeName = codeName;
        }

        public String getManufacturer() {

            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {

            this.manufacturer = manufacturer;
        }

        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }

    }

    /**
     * WebBrowser JavaScriptEngine.
     * @see <a href="https://en.wikipedia.org/wiki/JavaScript_engine">JavaScript engine</a>
     * @author Kahle
     */
    public static class JavaScriptEngine extends AbstractExtraData implements Serializable {
        private String name;
        private String version;
        private String codeName;
        private String manufacturer;
        private String description;

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }

        public String getVersion() {

            return version;
        }

        public void setVersion(String version) {

            this.version = version;
        }

        public String getCodeName() {

            return codeName;
        }

        public void setCodeName(String codeName) {

            this.codeName = codeName;
        }

        public String getManufacturer() {

            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {

            this.manufacturer = manufacturer;
        }

        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }

    }

}
