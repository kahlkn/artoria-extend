package artoria.data;

import java.io.Serializable;

/**
 * Operating system.
 * @see <a href="https://en.wikipedia.org/wiki/Operating_system">Operating system</a>
 * @author Kahle
 */
public class OperatingSystem extends AbstractExtraData implements Serializable {
    /**
     * The name of the operating system.
     */
    private String name;
    /**
     * The version of the operating system.
     */
    private String version;
    /**
     * The bits of the operating system.
     * @see <a href="https://en.wikipedia.org/wiki/64-bit_computing">64-bit computing</a>
     * @see <a href="https://en.wikipedia.org/wiki/32-bit">32-bit</a>
     */
    private String bits;
    /**
     * The code name of the operating system.
     */
    private String codeName;
    /**
     * The kernel name of the operating system.
     * @see <a href="https://en.wikipedia.org/wiki/Kernel_(operating_system)">Kernel (operating system)</a>
     */
    private String kernelName;
    /**
     * The kernel version of the operating system.
     */
    private String kernelVersion;
    /**
     * The manufacturer of the operating system.
     */
    private String manufacturer;
    /**
     * The description of the operating system.
     */
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

    public String getBits() {

        return bits;
    }

    public void setBits(String bits) {

        this.bits = bits;
    }

    public String getCodeName() {

        return codeName;
    }

    public void setCodeName(String codeName) {

        this.codeName = codeName;
    }

    public String getKernelName() {

        return kernelName;
    }

    public void setKernelName(String kernelName) {

        this.kernelName = kernelName;
    }

    public String getKernelVersion() {

        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {

        this.kernelVersion = kernelVersion;
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
