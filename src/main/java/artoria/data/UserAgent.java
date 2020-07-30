package artoria.data;

import java.io.Serializable;

public class UserAgent extends AbstractExtraData implements Serializable {
    private String content;
    private String description;
    private String device;
    private String deviceType;
    private String deviceBrandName;
    private String deviceModel;
    private String deviceCodeName;
    private String deviceManufacturer;
    private String devicePointingMethod;
    private String deviceDescription;
    private String operatingSystem;
    private String operatingSystemType;
    private String operatingSystemVersion;
    private String operatingSystemCodeName;
    private String operatingSystemBits;
    private String operatingSystemManufacturer;
    private String operatingSystemDescription;
    private String browser;
    private String browserType;
    private String browserVersion;
    private String browserCodeName;
    private String browserBits;
    private String browserMode;
    private String browserManufacturer;
    private String cssVersion;
    private Boolean alpha;
    private Boolean beta;
    private Boolean syndicationReader;
    private String browserDescription;
    private String renderingEngineName;
    private String renderingEngineVersion;
    private String renderingEngineManufacturer;
    private String renderingEngineDescription;
//    private Boolean frames;
//    private Boolean iFrames;
//    private Boolean tables;
//    private Boolean cookies;
//    private Boolean backgroundSounds;
//    private Boolean javaScript;
//    private Boolean vbScript;
//    private Boolean javaApplets;
//    private Boolean activeXControls;

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getDevice() {

        return device;
    }

    public void setDevice(String device) {

        this.device = device;
    }

    public String getDeviceType() {

        return deviceType;
    }

    public void setDeviceType(String deviceType) {

        this.deviceType = deviceType;
    }

    public String getDeviceBrandName() {

        return deviceBrandName;
    }

    public void setDeviceBrandName(String deviceBrandName) {

        this.deviceBrandName = deviceBrandName;
    }

    public String getDeviceModel() {

        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {

        this.deviceModel = deviceModel;
    }

    public String getDeviceCodeName() {

        return deviceCodeName;
    }

    public void setDeviceCodeName(String deviceCodeName) {

        this.deviceCodeName = deviceCodeName;
    }

    public String getDeviceManufacturer() {

        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {

        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDevicePointingMethod() {

        return devicePointingMethod;
    }

    public void setDevicePointingMethod(String devicePointingMethod) {

        this.devicePointingMethod = devicePointingMethod;
    }

    public String getDeviceDescription() {

        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {

        this.deviceDescription = deviceDescription;
    }

    public String getOperatingSystem() {

        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {

        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystemType() {

        return operatingSystemType;
    }

    public void setOperatingSystemType(String operatingSystemType) {

        this.operatingSystemType = operatingSystemType;
    }

    public String getOperatingSystemVersion() {

        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String operatingSystemVersion) {

        this.operatingSystemVersion = operatingSystemVersion;
    }

    public String getOperatingSystemCodeName() {

        return operatingSystemCodeName;
    }

    public void setOperatingSystemCodeName(String operatingSystemCodeName) {

        this.operatingSystemCodeName = operatingSystemCodeName;
    }

    public String getOperatingSystemBits() {

        return operatingSystemBits;
    }

    public void setOperatingSystemBits(String operatingSystemBits) {

        this.operatingSystemBits = operatingSystemBits;
    }

    public String getOperatingSystemManufacturer() {

        return operatingSystemManufacturer;
    }

    public void setOperatingSystemManufacturer(String operatingSystemManufacturer) {

        this.operatingSystemManufacturer = operatingSystemManufacturer;
    }

    public String getOperatingSystemDescription() {

        return operatingSystemDescription;
    }

    public void setOperatingSystemDescription(String operatingSystemDescription) {

        this.operatingSystemDescription = operatingSystemDescription;
    }

    public String getBrowser() {

        return browser;
    }

    public void setBrowser(String browser) {

        this.browser = browser;
    }

    public String getBrowserType() {

        return browserType;
    }

    public void setBrowserType(String browserType) {

        this.browserType = browserType;
    }

    public String getBrowserVersion() {

        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {

        this.browserVersion = browserVersion;
    }

    public String getBrowserCodeName() {

        return browserCodeName;
    }

    public void setBrowserCodeName(String browserCodeName) {

        this.browserCodeName = browserCodeName;
    }

    public String getBrowserBits() {

        return browserBits;
    }

    public void setBrowserBits(String browserBits) {

        this.browserBits = browserBits;
    }

    public String getBrowserMode() {

        return browserMode;
    }

    public void setBrowserMode(String browserMode) {

        this.browserMode = browserMode;
    }

    public String getBrowserManufacturer() {

        return browserManufacturer;
    }

    public void setBrowserManufacturer(String browserManufacturer) {

        this.browserManufacturer = browserManufacturer;
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

    public String getBrowserDescription() {

        return browserDescription;
    }

    public void setBrowserDescription(String browserDescription) {

        this.browserDescription = browserDescription;
    }

    public String getRenderingEngineName() {

        return renderingEngineName;
    }

    public void setRenderingEngineName(String renderingEngineName) {

        this.renderingEngineName = renderingEngineName;
    }

    public String getRenderingEngineVersion() {

        return renderingEngineVersion;
    }

    public void setRenderingEngineVersion(String renderingEngineVersion) {

        this.renderingEngineVersion = renderingEngineVersion;
    }

    public String getRenderingEngineManufacturer() {

        return renderingEngineManufacturer;
    }

    public void setRenderingEngineManufacturer(String renderingEngineManufacturer) {

        this.renderingEngineManufacturer = renderingEngineManufacturer;
    }

    public String getRenderingEngineDescription() {

        return renderingEngineDescription;
    }

    public void setRenderingEngineDescription(String renderingEngineDescription) {

        this.renderingEngineDescription = renderingEngineDescription;
    }

}
