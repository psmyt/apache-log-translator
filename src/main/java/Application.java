import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Application {
    public static void main(String[] args) throws DecoderException {
        if (args.length != 1)
            throw new IllegalArgumentException("expected one argument, but was supplied: " + args.length);

        Pattern byteLiteralExtractor = Pattern.compile("^\\[0x([0-9a-f]{1,2})](.*)$", Pattern.DOTALL);
        Pattern lineBreakExtractor = Pattern.compile("^\\[\\\\([r,n])](.*)$", Pattern.DOTALL);

        String str = args[0];
        List<Byte> bytes = new ArrayList<>();

        while (str.length() != 0) {
            Matcher byteLiteralMatcher = byteLiteralExtractor.matcher(str);
            Matcher lineBreakMatcher = lineBreakExtractor.matcher(str);

            if (byteLiteralMatcher.matches()) {
                String hex = Optional.of(byteLiteralMatcher.group(1))
                        .filter(s -> s.length() > 1)
                        .orElse("0" + byteLiteralMatcher.group(1));
                bytes.add(Hex.decodeHex(hex)[0]);
                str = byteLiteralMatcher.group(2);
                continue;
            }

            if (lineBreakMatcher.matches()) {
                if (lineBreakMatcher.group(1).equals("r")) {
                    bytes.add((byte) 0x0D);
                } else {
                    bytes.add((byte) 0x0A);
                }
                str = lineBreakMatcher.group(2);
                continue;
            }

            bytes.add((byte) str.charAt(0));
            str = str.substring(1);
        }

        byte[] result = bytes.stream()
                .collect(
                        ByteArrayOutputStream::new,
                        ByteArrayOutputStream::write,
                        (b1, b2) -> b1.write(b2.toByteArray(), 0, b2.size())
                )
                .toByteArray();

        if (isGzip(result)) {
            System.out.println("gzip detected, decompressing...");
            try (GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(new ByteArrayInputStream(result))) {
                result = gzipInputStream.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(new String(result));
    }

    private static boolean isGzip(byte[] bytes) {
        return bytes.length > 1 &&
                bytes[0] == (byte) GZIPInputStream.GZIP_MAGIC &&
                bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8) ;
    }
}
