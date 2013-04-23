
class Options {
    // Mask values
    public static final int COMPUTE_SIMILARITY  = 0x01;
    public static final int COMPUTE_CLUSTERS    = 0x02;
    public static final int MULTI_PARTITE       = 0x04;
    public static final int DIRECTED            = 0x08;
    public static final int WEIGHTED            = 0x10;
    public static final int IN_MEMORY           = 0x20;

    public static boolean isSet(final int option, int mask) {
        return ((mask & (1 << option)) != 0);
    }
}
