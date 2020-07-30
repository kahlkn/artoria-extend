package artoria.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "artoria.swagger")
public class SwaggerProperties {
    private Boolean enabled;
    private String basePackage = "com";
    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String contact;
    private String license;
    private String licenseUrl;
    private String version;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getBasePackage() {

        return basePackage;
    }

    public void setBasePackage(String basePackage) {

        this.basePackage = basePackage;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getTermsOfServiceUrl() {

        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {

        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getContact() {

        return contact;
    }

    public void setContact(String contact) {

        this.contact = contact;
    }

    public String getLicense() {

        return license;
    }

    public void setLicense(String license) {

        this.license = license;
    }

    public String getLicenseUrl() {

        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {

        this.licenseUrl = licenseUrl;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

}
