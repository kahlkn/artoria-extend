package artoria.identifier;

import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.ZERO;
import static artoria.common.Constants.ZERO_STR;

/**
 * Abstract increment identifier generator.
 * @author Kahle
 */
public abstract class AbstractIncrIdGenerator implements StringIdentifierGenerator {
    private TimeUnit expireTimeUnit = TimeUnit.MILLISECONDS;
    private long expireTime = -1;
    private long offset = 0L;
    private int numberLength = 6;
    private int stepLength = 1;
    private String datePattern;
    private String prefix;
    private String name;

    public TimeUnit getExpireTimeUnit() {

        return expireTimeUnit;
    }

    public void setExpireTimeUnit(TimeUnit expireTimeUnit) {

        this.expireTimeUnit = expireTimeUnit;
    }

    public long getExpireTime() {

        return expireTime;
    }

    public void setExpireTime(long expireTime) {

        this.expireTime = expireTime;
    }

    public long getOffset() {

        return offset;
    }

    public void setOffset(long offset) {

        this.offset = offset;
    }

    public int getNumberLength() {

        return numberLength;
    }

    public void setNumberLength(int numberLength) {

        this.numberLength = numberLength;
    }

    public int getStepLength() {

        return stepLength;
    }

    public void setStepLength(int stepLength) {

        this.stepLength = stepLength;
    }

    public String getDatePattern() {

        return datePattern;
    }

    public void setDatePattern(String datePattern) {

        this.datePattern = datePattern;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    /**
     * Increment and get the stored value.
     * @return The value that is incremented and taken out
     */
    protected abstract Long incrementAndGet();

    @Override
    public Object nextIdentifier() {

        return nextStringIdentifier();
    }

    @Override
    public String nextStringIdentifier() {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        // Increment value.
        Long increment = incrementAndGet();
        Assert.notNull(increment,
                "Failed to invoke \"incrementAndGet\". "
        );
        // Add offset.
        if (offset > ZERO) { increment += offset; }
        // Create identifier builder.
        StringBuilder identifier = new StringBuilder();
        identifier.append(increment);
        // Handle number length.
        int count;
        if (numberLength > ZERO
                && (count = numberLength - identifier.length()) > ZERO) {
            for (; count > ZERO; count--) {
                identifier.insert(ZERO, ZERO_STR);
            }
        }
        // Handle date string.
        if (StringUtils.isNotBlank(datePattern)) {
            String format = DateUtils.format(datePattern);
            identifier.insert(ZERO, format);
        }
        // Handle prefix.
        if (StringUtils.isNotBlank(prefix)) {
            identifier.insert(ZERO, prefix);
        }
        return identifier.toString();
    }

}
