public enum KeySignature {
    //Major keys
    gFlat_M(-6),
    dFlat_M(-5),
    AFlat_M(-4),
    eFlat_M(-3),
    bFlat_M(-2),
    f_M(-1),
    c_M(0),
    g_M(1),
    d_M(2),
    a_M(3),
    e_M(4),
    b_M(5),
    fSharp_M(6),

    // Minor keys
    eFlat_m(-6),
    bFlat_m(-5),
    f_min(-4),
    c_min(-3),
    g_min(-2),
    d_min(-1),
    a_min(0),
    e_min(1),
    b_min(2),
    fSharp_min(3),
    cSharp_min(4),
    gSharp_min(5),
    dSharp_min(6);

    public final int value;

    KeySignature(int i) {
        this.value=i;
    }
}
