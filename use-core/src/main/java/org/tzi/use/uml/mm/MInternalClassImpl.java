package org.tzi.use.uml.mm;

public class MInternalClassImpl extends MClassImpl{
    MInternalClassImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
    }

    @Override
    public String nameAsRolename() {
        String rolename = name();
        if(name().contains("@")) {
            rolename = rolename.split("@")[1];
        }
        return Character.toLowerCase(rolename.charAt(0)) + rolename.substring(1);
    }
}
