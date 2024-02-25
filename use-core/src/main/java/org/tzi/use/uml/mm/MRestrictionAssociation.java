package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.NonNull;

public class MRestrictionAssociation extends MGeneralization {


    private String oldRoleNameEnd1;
    private String oldRoleNameEnd2;
    private String newRoleNameEnd1;
    private String newRoleNameEnd2;
    /**
     * Creates a new generalization.
     *
     * @param child
     * @param parent
     */
    MRestrictionAssociation(@NonNull MClassifier child, @NonNull MClassifier parent) {
        super(child, parent);
    }

    public void addRoleMapping1(String oldRole, String newRole) {
        this.oldRoleNameEnd1 = oldRole;
        this.newRoleNameEnd1 = newRole;
    }
    public void addRoleMapping2(String oldRole, String newRole) {
        this.oldRoleNameEnd2 = oldRole;
        this.newRoleNameEnd2 = newRole;
    }



}
