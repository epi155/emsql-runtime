package io.github.epi155.emsql.fpe;

public final class PanMasker extends PanScrubber {

    private static final String MASK = "********";

    public PanMasker(boolean luhnCheck, boolean argsMask) {
        super(luhnCheck, argsMask);
    }

    public String mask(String text) {
        return scrub(text);
    }

    @Override
    String transform(String pan) {
        return pan.substring(0, 4) + MASK + pan.substring(pan.length() - 4);
    }

    @Override
    String decrypt(String pan) {
        return pan;
    }

}
