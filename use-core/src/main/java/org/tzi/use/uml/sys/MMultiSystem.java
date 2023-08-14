package org.tzi.use.uml.sys;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMultiSystem {

    private MMultiModel fMultiModel;
    private Map<String, MSystem> fSystems;

    public MMultiSystem(MMultiModel fMultiModel) {
        this.fMultiModel = fMultiModel;
        this.fSystems = new HashMap<>();

        for (MModel model : fMultiModel.models()){
            MSystem modelSystem = new MSystem(model);
            fSystems.put(model.name(), modelSystem);
        }

    }

    public void setModelSystem(String modelName, MSystem system){
        fSystems.replace(modelName, system);
    }

    public MSystem toMSystem() throws Exception{
        String delimiter= "_";
        MModel convertedModel = fMultiModel.toMModel();
        MSystem convertedSystem = new MSystem(convertedModel);

        //loop over all the previous systems and add the actions to the new system

        for (MSystem system : fSystems.values()){
            //iterate the objects
            for (MObject object : system.state().allObjects()){
                if (object instanceof MObjectImpl) {
                    String clsName = system.model().name() + delimiter + object.cls().name();
                    MClass convertedClass = convertedModel.getClass(clsName);
                    String newObjName = system.model().name() + delimiter + object.name();
                    MObject convertedObject = convertedSystem.state().createObject(convertedClass, newObjName);
                    for(MAttribute attr : convertedObject.cls().attributes()) {
                        Value convertedValue = system.state().getObjectState(object).attributeValue(attr.name());
                        MAttribute newAttr = convertedClass.attribute(attr.name(),true);
                        convertedSystem.state().getObjectState(convertedObject).setAttributeValue(newAttr, convertedValue);
                    }
                }
            }


            //iterate the links
            for (MLink link : system.state().allLinks()){
                if (link instanceof MLinkObjectImpl){
                    String assocName = system.model().name() + delimiter + link.association().name();
                    MAssociationClass convertedAssoc = (MAssociationClass) convertedModel.getAssociation(assocName);
                    if (convertedAssoc == null)
                        throw new Exception("association with name: " + assocName + " was not found");
                    List<MObject> objects = new ArrayList<>();
                    for (MObject linkObj : link.linkedObjects()) {
                        String convertedObjName = system.model().name() + delimiter + linkObj.name();
                        MObject convertedObj = convertedSystem.state().objectByName(convertedObjName);
                        objects.add(convertedObj);
                        for(MAttribute attr : convertedObj.cls().attributes()) {
                            Value convertedValue = system.state().getObjectState(linkObj).attributeValue(attr.name());
                            convertedSystem.state().getObjectState(convertedObj).setAttributeValue(attr, convertedValue);
                        }
                    }
                    String convertedLinkObjName = system.model().name() + delimiter + ((MLinkObjectImpl) link).name();
                    convertedSystem.state().createLinkObject(convertedAssoc, convertedLinkObjName, objects, link.getQualifier());
                }
            }

            for (MLink link : system.state().allLinks()){
                if (link instanceof MLinkImpl) {
                    String assocName = system.model().name() + delimiter + link.association().name();
                    MAssociation convertedAssoc = convertedModel.getAssociation(assocName);
                    if (convertedAssoc == null)
                        throw new Exception("association with name: " + assocName + " was not found");
                    List<MObject> objects = new ArrayList<>();
                    for (MObject linkObj : link.linkedObjects()) {
                        String convertedObjName = system.model().name() + delimiter + linkObj.name();
                        MObject convertedObj = convertedSystem.state().objectByName(convertedObjName);
                        objects.add(convertedObj);
                    }
                    convertedSystem.state().createLink(convertedAssoc, objects, link.getQualifier());
                }
            }

        }

        return convertedSystem;
    }

    //num of total objects from all systems
    public int numObjects() {
        return fSystems.values()
                .stream()
                .map(system -> system.state().numObjects())
                .reduce(0, Integer::sum);
    }

    //num of total links from all systems
    public int numLinks() {
        return fSystems.values()
                .stream()
                .map(system -> system.state().allLinks().size())
                .reduce(0, Integer::sum);
    }



}
