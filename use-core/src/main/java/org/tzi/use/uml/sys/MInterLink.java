package org.tzi.use.uml.sys;

public class MInterLink {

    public final String associationName;
    public final String objNameSrc;
    public final String objNameDst;

    public MInterLink(String associationName, String objNameSrc, String objNameDst) {
        this.associationName = associationName;
        this.objNameSrc = objNameSrc;
        this.objNameDst = objNameDst;
    }
}
