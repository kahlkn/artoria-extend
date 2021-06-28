package artoria.codec;

import static artoria.common.Constants.MINUS_ONE;
import static artoria.common.Constants.ZERO;

public class ApacheBase64 extends Base64 {
    private org.apache.commons.codec.binary.Base64 apacheBase64;

    public ApacheBase64() {

        this(false, false, MINUS_ONE, null);
    }

    public ApacheBase64(boolean urlSafe) {

        this(urlSafe, false, MINUS_ONE, null);
    }

    public ApacheBase64(boolean mime, int lineLength, byte[] lineSeparator) {

        this(false, mime, lineLength, lineSeparator);
    }

    protected ApacheBase64(boolean urlSafe, boolean mime, int lineLength, byte[] lineSeparator) {
        super(urlSafe, mime, lineLength, lineSeparator);
        if (mime) {
            lineLength = lineLength > ZERO ? lineLength : DEFAULT_MIME_LINE_LENGTH;
        }
        else { lineLength = MINUS_ONE; }
        apacheBase64 = new org.apache.commons.codec.binary.Base64(
                lineLength, lineSeparator, urlSafe
        );
    }

    @Override
    public byte[] encode(byte[] source) {

        return apacheBase64.encode(source);
    }

    @Override
    public byte[] decode(byte[] source) {

        return apacheBase64.decode(source);
    }

}
