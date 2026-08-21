package io.github.epi155.emsql.fpe;

public final class PciSanitizer {

    private PciSanitizer() {
    }

    public static String sanitize(String text) {
        PanScrubber scrubber = PciConfig.get();
        return scrubber == null ? text : scrubber.scrub(text);
    }

    public static String decodePan(String encPan) {
        PanScrubber scrubber = PciConfig.get();
        return scrubber == null ? encPan : scrubber.decrypt(encPan);
    }
    public static String encodePan(String pan) {
        PanScrubber scrubber = PciConfig.get();
        return scrubber == null ? pan : scrubber.encrypt(pan);
    }
    public static Object sanitizeArg(Object o) {
        PanScrubber scrubber = PciConfig.get();
        return scrubber == null ? o : scrubber.scrubArg(o);
    }
}