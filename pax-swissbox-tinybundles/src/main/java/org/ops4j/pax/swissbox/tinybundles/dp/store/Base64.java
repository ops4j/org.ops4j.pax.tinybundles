package org.ops4j.pax.swissbox.tinybundles.dp.store;

/**
 * Created by IntelliJ IDEA.
 * User: tonit
 * Date: Jul 2, 2009
 * Time: 9:27:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base64 {

    public static String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static int splitLinesAt = 76;

    public static byte[] zeroPad(int length, byte[] bytes) {
        byte[] padded = new byte[length]; // initialized to zero by JVM
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }

    public static String encode(String string) {

        String encoded = "";
        byte[] stringArray;
        try {
            stringArray = string.getBytes("UTF-8");  // use appropriate encoding string!
        } catch (Exception ignored) {
            stringArray = string.getBytes();  // use locale default rather than croak
        }
        // determine how many padding bytes to add to the output
        int paddingCount = (3 - (string.length() % 3)) % 3;
        // add any necessary padding to the input
        stringArray = zeroPad(stringArray.length + paddingCount, stringArray);
        // process 3 bytes at a time, churning out 4 output bytes
        // worry about CRLF insertions later
        for (int i = 0; i < stringArray.length; i += 3) {
            int j = (stringArray[i] << 16) + (stringArray[i + 1] << 8) +
                stringArray[i + 2];
            encoded = encoded + base64code.charAt((j >> 18) & 0x3f) +
                base64code.charAt((j >> 12) & 0x3f) +
                base64code.charAt((j >> 6) & 0x3f) +
                base64code.charAt(j & 0x3f);
        }
        // replace encoded padding nulls with "="
        return encoded;

    }
   
    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {

            System.err.println("encoding \"" + args[i] + "\"");
            System.out.println(encode(args[i]));

        }

    }

}

