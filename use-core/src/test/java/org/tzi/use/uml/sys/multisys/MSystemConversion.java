package org.tzi.use.uml.sys.multisys;

import junit.framework.TestCase;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.TestModelUtil;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;

public class MSystemConversion extends TestCase {

    public void test1() {
        try{
            MModel model1 = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass();
            MSystem system1 = new MSystem( model1 );

            MModel model2 = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass();
            MSystem system2 = new MSystem( model2 );


            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            MModel convertedModel = multiModel.toMModel();
            MSystem convertedSystem = new MSystem(convertedModel);







        } catch (Exception e) {
            throw new Error(e);
        }

    }
}
