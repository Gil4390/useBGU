package org.tzi.use.uml.mm;

public class MInterClassImpl extends MClassImpl{

    public MClassImpl originalClass;

    MInterClassImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
    }

    public void setOriginalClass(MClassImpl originalClass){
        this.originalClass = originalClass;
    }


    public MInterClassImpl initCopy(String prefix) {
        String newName = prefix + this.name();

        MInterClassImpl cMClass = new MInterClassImpl(newName, this.isAbstract());
        return cMClass;
    }

    public MInterClassImpl makeCopy(MInterClassImpl cMClass,String prefix, MModel newModel) throws Exception {

        cMClass.setModel(newModel);
        for (String key : this.fAttributes.keySet()) {
            MAttribute attr = this.fAttributes.get(key);
            cMClass.addAttribute(attr);
        }

        for (String key : this.fOperations.keySet()) {
            MOperation oper = this.fOperations.get(key);
            MOperation cMOperation = oper.makeCopy(prefix);
            cMOperation.setClass(cMClass);
            cMClass.addOperation(cMOperation);
        }

        return cMClass;
    }
}