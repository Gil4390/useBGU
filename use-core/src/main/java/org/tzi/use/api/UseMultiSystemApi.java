package org.tzi.use.api;

import org.tzi.use.api.impl.UseSystemApiNative;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.StringUtil;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.Map;


public class UseMultiSystemApi {

    private Map<String,UseSystemApi> multiSystemMap;

    public UseMultiSystemApi() {
        this.multiSystemMap = new HashMap<>();
    }

    public void addSystemApi(MSystem system, boolean enableUndo) {
        this.multiSystemMap.put(system.model().name(),UseSystemApi.create(system, enableUndo));
    }

    public void removeSystemApi(MSystem system) {
        this.multiSystemMap.remove(system.model().name());
    }

    public boolean checkSystemState(String modelName) throws UseApiException {
        return this.getApiSafe(modelName).checkState();
    }

    public boolean checkAllSystemStates() {
        for(UseSystemApi api : this.multiSystemMap.values()) {
            if(!api.checkState())
                return false;
        }
        return true;
    }

    public MObject[] createObjects(String modelName, String objectClass, String... objectNames) throws UseApiException {
        return this.getApiSafe(modelName).createObjects(objectClass, objectNames);
    }

    public MObject createObjectEx(String modelName, MClass objectClass, String objectName) throws UseApiException {
        return this.getApiSafe(modelName).createObjectEx(objectClass,objectName);
    }


    public void setAttributeValueEx(String modelName, MObject object, MAttribute attribute, Value value) throws UseApiException {
        this.getApiSafe(modelName).setAttributeValueEx(object, attribute, value);
    }

    public MLink createLinkObject(String modelName, String associationClassName,
                                  String newObjectName,
                                  String... connectedObjectNames) throws UseApiException {
        return this.getApiSafe(modelName).createLinkObject(associationClassName, newObjectName, connectedObjectNames);
    }

    public MLink createLinkEx(String modelName, MAssociation association, MObject[] connectedObjects, Value[][] qualifierValues) throws UseApiException {
        return this.getApiSafe(modelName).createLinkEx(association, connectedObjects, qualifierValues);
    }


    public MLinkObject createLinkObjectEx(String modelName, MAssociationClass associationClass, String newObjectName, MObject[] connectedObjects, Value[][] qualifierValues) throws UseApiException {
        return this.getApiSafe(modelName).createLinkObjectEx(associationClass, newObjectName, connectedObjects, qualifierValues);
    }


    public void deleteObject(String modelName, String objectName) throws UseApiException {
        this.getApiSafe(modelName).deleteObject(objectName);
    }

    public void deleteObjects(String modelName, String... objectNames) throws UseApiException {
        this.getApiSafe(modelName).deleteObjects(objectNames);
    }

    public void deleteObjectEx(String modelName, MObject object) throws UseApiException {
        this.getApiSafe(modelName).deleteObjectEx(object);
    }


    public void deleteLinkEx(String modelName, MAssociation association, MObject[] connectedObjects, Value[][] qualifierValues) throws UseApiException {
        this.getApiSafe(modelName).deleteLinkEx(association, connectedObjects, qualifierValues);
    }


    public void deleteLinkEx(String modelName, MLink link) throws UseApiException {
        this.getApiSafe(modelName).deleteLinkEx(link);
    }


    public void undo(String modelName) throws UseApiException, OperationNotSupportedException {
        this.getApiSafe(modelName).undo();
    }


    public void redo(String modelName) throws UseApiException, OperationNotSupportedException {
        this.getApiSafe(modelName).redo();
    }

    public UseSystemApi getApiSafe(String modelName) throws UseApiException {
        UseSystemApi api = this.multiSystemMap.get(modelName);
        if(api == null) {
            throw new UseApiException("No existing api "+ StringUtil.inQuotes(modelName));
        }
        return api;
    }
}
