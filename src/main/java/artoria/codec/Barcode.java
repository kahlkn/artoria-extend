package artoria.codec;

import artoria.exception.ExceptionUtils;
import artoria.file.FilenameUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static artoria.common.Constants.TWO;
import static artoria.common.Constants.ZERO;

/**
 * Barcode encode and decode tools.
 * @author Kahle
 */
public class Barcode {
    private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    private static final String DEFAULT_FILE_EXTENSION = ".jpg";
    private static final int DEFAULT_TRUE_COLOR = 0xFF000000;
    private static final int DEFAULT_FALSE_COLOR = 0xFFFFFFFF;

    public static Barcode create() {

        return new Barcode();
    }

    public static Barcode create(int width, int height) {

        return new Barcode().setWidth(width).setHeight(height);
    }

    public static BufferedImage toBufferedImage(File file) throws IOException {

        return ImageIO.read(file);
    }

    public static BufferedImage toBufferedImage(BitMatrix bitMatrix, int imageType, int trueColor, int falseColor) {
        int width = bitMatrix.getWidth(), height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, imageType);
        for (int x = ZERO; x < width; x++ ) {
            for (int y = ZERO; y < height; y++ ) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? trueColor : falseColor);
            }
        }
        return image;
    }

    private Map<EncodeHintType, Object> encodeHints = new HashMap<EncodeHintType, Object>();
    private Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H;
    private BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
    private String charset = Charset.defaultCharset().name();
    private int falseColor = DEFAULT_FALSE_COLOR;
    private int trueColor = DEFAULT_TRUE_COLOR;
    private int imageType = DEFAULT_IMAGE_TYPE;
    private int width = 500;
    private int height = 500;
    private int margin = 1;
    private File logo;

    private Barcode() {
    }

    private void handleHints() {
        this.encodeHints.put(EncodeHintType.CHARACTER_SET, this.charset);
        this.encodeHints.put(EncodeHintType.ERROR_CORRECTION, this.errorCorrectionLevel);
        this.encodeHints.put(EncodeHintType.MARGIN, this.margin);
        this.decodeHints.put(DecodeHintType.CHARACTER_SET, this.charset);
    }

    private void handleLogo(BufferedImage image) {
        if (logo == null) { return; }
        Assert.state(logo.exists(), "Parameter \"logo\" must exist. ");
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double rate;
        switch (errorCorrectionLevel) {
            case L:{ rate = 0.20; } break;
            case M:{ rate = 0.25; } break;
            case Q:{ rate = 0.27; } break;
            case H:{ rate = 0.28; } break;
            default:{
                throw new IllegalArgumentException("Parameter \"errorCorrectionLevel\" is illegal. ");
            }
        }
        int logoWidth = Double.valueOf(imageWidth * rate).intValue();
        int logoHeight = Double.valueOf(imageHeight * rate).intValue();
        int logoX = (imageWidth - logoWidth) / TWO;
        int logoY = (imageHeight - logoHeight) / TWO;
        try {
            BufferedImage logoImage = ImageIO.read(logo);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
            graphics.dispose();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public Map<EncodeHintType, Object> getEncodeHints() {

        return encodeHints;
    }

    public Barcode setEncodeHints(Map<EncodeHintType, Object> encodeHints) {
        this.encodeHints = encodeHints;
        return this;
    }

    public Barcode addEncodeHints(Map<EncodeHintType, Object> encodeHints) {
        this.encodeHints.putAll(encodeHints);
        return this;
    }

    public Barcode addEncodeHint(EncodeHintType encodeHintType, Object value) {
        encodeHints.put(encodeHintType, value);
        return this;
    }

    public Map<DecodeHintType, Object> getDecodeHints() {

        return decodeHints;
    }

    public Barcode setDecodeHints(Map<DecodeHintType, Object> decodeHints) {
        this.decodeHints = decodeHints;
        return this;
    }

    public Barcode addDecodeHints(Map<DecodeHintType, Object> decodeHints) {
        this.decodeHints.putAll(decodeHints);
        return this;
    }

    public Barcode addDecodeHint(DecodeHintType decodeHintType, Object value) {
        decodeHints.put(decodeHintType, value);
        return this;
    }

    public ErrorCorrectionLevel getErrorCorrectionLevel() {

        return errorCorrectionLevel;
    }

    public Barcode setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
        return this;
    }

    public BarcodeFormat getBarcodeFormat() {

        return barcodeFormat;
    }

    public Barcode setBarcodeFormat(BarcodeFormat barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
        return this;
    }

    public String getCharset() {

        return charset;
    }

    public Barcode setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public int getFalseColor() {

        return falseColor;
    }

    public Barcode setFalseColor(int falseColor) {
        this.falseColor = falseColor;
        return this;
    }

    public int getTrueColor() {

        return trueColor;
    }

    public Barcode setTrueColor(int trueColor) {
        this.trueColor = trueColor;
        return this;
    }

    public int getImageType() {

        return imageType;
    }

    public Barcode setImageType(int imageType) {
        this.imageType = imageType;
        return this;
    }

    public int getWidth() {

        return width;
    }

    public Barcode setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {

        return height;
    }

    public Barcode setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getMargin() {

        return margin;
    }

    public Barcode setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    public File getLogo() {

        return logo;
    }

    public Barcode setLogo(File logo) {
        this.logo = logo;
        return this;
    }

    public BitMatrix encodeToBitMatrix(String content) throws WriterException {
        handleHints();
        MultiFormatWriter writer = new MultiFormatWriter();
        return writer.encode(content, barcodeFormat, width, height, encodeHints);
    }

    public Result decodeFromBufferedImage(BufferedImage image) throws NotFoundException {
        handleHints();
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        MultiFormatReader reader = new MultiFormatReader();
        return reader.decode(binaryBitmap, decodeHints);
    }

    public String decode(BufferedImage image) throws NotFoundException {
        Result result = decodeFromBufferedImage(image);
        return result != null ? result.getText() : null;
    }

    public BufferedImage encode(String content) throws WriterException {
        BitMatrix bitMatrix = encodeToBitMatrix(content);
        BufferedImage bufferedImage =
                Barcode.toBufferedImage(bitMatrix, imageType, trueColor, falseColor);
        handleLogo(bufferedImage);
        return bufferedImage;
    }

    public String decodeFromFile(File file) throws NotFoundException, IOException {
        BufferedImage image = Barcode.toBufferedImage(file);
        return decode(image);
    }

    public boolean encodeToFile(String content, File file) throws WriterException, IOException {
        BufferedImage image = encode(content);
        String extension = FilenameUtils.getExtension(file.toString());
        if (StringUtils.isBlank(extension)) {
            file = new File(file.toString(), DEFAULT_FILE_EXTENSION);
            extension = FilenameUtils.getExtension(file.toString());
        }
        return ImageIO.write(image, extension, file);
    }

}
