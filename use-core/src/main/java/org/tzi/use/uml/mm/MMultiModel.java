package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

public class MMultiModel {
    private Map<String, MModel> fModels; // <modelName, MModel>
    private String fName;
    private String fFilename; // name of .use file

    public MMultiModel(String name) {
        fName = name;
        fModels = new TreeMap<>();
        fFilename = "";
    }

    public void addModel(MModel model) throws Exception { //TODO: add custom exception
        if (fModels.containsKey(model.name()))
            throw new Exception("MultiModel already contains a model `"
                    + model.name() + "'.");
        // TODO: find out if we need to check more exceptions
        fModels.put(model.name(), model);
        // TODO: add to GenGraph
        //model.setMLModel(this); TODO: add setMLModel method
    }

    public void removeModel(String modelName) throws Exception { //TODO: add custom exception
        if (!fModels.containsKey(modelName))
            throw new Exception("MultiModel does not contain a model `" + modelName + "'.");

        // TODO: find out if we need to check more exceptions
        fModels.remove(modelName);
    }

    public void setFilename(String name) {
        fFilename = name;
    }


    public Collection<MModel> models() {
        return fModels.values();
    }

    public int size(){return fModels.size();}

    public int numOfClasses() {
        return fModels.values()
                .stream()
                .mapToInt(model -> model.classes().size())
                .sum();
    }


    public int maxNumOfClasses() {
        return fModels.values()
                .stream()
                .mapToInt(model -> model.classes().size())
                .max()
                .orElse(-1);
    }

    public boolean containsDuplicateClassNames() {
        Set<String> allNames = new HashSet<>();
        for (MModel model : fModels.values()) {
            List<String> classNames = model.classes().stream()
                    .map(cls -> cls.name())
                    .collect(Collectors.toList());
            for (String cname : classNames) {
                if (!allNames.add(cname)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}