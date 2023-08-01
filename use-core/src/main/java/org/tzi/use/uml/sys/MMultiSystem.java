package org.tzi.use.uml.sys;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;

import java.util.HashMap;
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
        MModel convertedModel = fMultiModel.toMModel();
        MSystem convertedSystem = new MSystem(convertedModel);

        //loop over all the previous systems and add the actions to the new system

        for (MSystem system : fSystems.values()){
            //iterate the objects
            for (MObject object : system.state().allObjects()){
                String clsName = system.model().name() + "_" + object.cls().name();
                MClass convertedClass = convertedModel.getClass(clsName);
                String newObjName = system.model().name() + "_" + object.name();
                convertedSystem.createObject(convertedClass, newObjName);
            }

            //iterate the links
            for (MLink link : system.state().allLinks()){
                //convertedSystem.createLinkObject()
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
