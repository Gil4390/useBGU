package org.tzi.use.api;

import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.ModelFactory;

public class UseMultiModelApi {

    private MMultiModel mMultiModel;
    ModelFactory mFactory = new ModelFactory();

    public UseMultiModelApi() {
        mMultiModel = mFactory.createMultiModel("unnamed");
    }

    public UseMultiModelApi(String modelName) {
        mMultiModel = mFactory.createMultiModel(modelName);
    }

    public UseMultiModelApi(MMultiModel multimodel) {
        mMultiModel = multimodel;
    }





    public MMultiModel getMultiModel() {
        return mMultiModel;
    }


    public void addModel(MModel model) throws Exception {
        mMultiModel.addModel(model);
    }
}
