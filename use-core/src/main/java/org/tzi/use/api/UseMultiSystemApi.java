package org.tzi.use.api;

import org.tzi.use.api.impl.UseSystemApiNative;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.*;
import org.tzi.use.util.StringUtil;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.Map;


public class UseMultiSystemApi {

    //private Map<String,UseSystemApi> multiSystemMap;

    private MMultiSystem multiSystem;
    private UseMultiModelApi multiModelApi;

    public UseMultiSystemApi() {

    }

    public UseMultiSystemApi(MMultiModel multi, boolean enableUndo) {
        this.multiModelApi = new UseMultiModelApi(multi);
        this.multiSystem = new MMultiSystem(multi);
    }

    public UseMultiSystemApi(MMultiSystem multiSys, boolean enableUndo) {
        this.multiModelApi = new UseMultiModelApi(multiSys.multiModel());
        this.multiSystem = multiSys;
    }

    public MMultiSystem getMultiSystem() {
        return multiSystem;
    }

    public UseMultiModelApi getMultiModelApi() {
        return multiModelApi;
    }

    public void addSystemApi(MSystem system, boolean enableUndo) {
        this.multiSystem.fSystems.put(system.model().name(),system);
    }

    public void removeSystemApi(MSystem system) {
        this.multiSystem.fSystems.remove(system.model().name());
    }

    public boolean checkSystemState(String modelName) throws UseApiException {
        return this.getApiSafe(modelName).checkState();
    }

    public boolean checkAllSystemStates() {
        for(MSystem sys : this.multiSystem.systems()) {
            UseSystemApi api = new UseSystemApiNative(sys);
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

    public final Value setAttributeValue(
            String modelName,
            String objectName,
            String attributeName,
            String valueExpression) throws UseApiException {

        return getApiSafe(modelName).setAttributeValue(objectName, attributeName, valueExpression);
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
        MSystem sys = multiSystem.fSystems.get(modelName);
        if(sys == null) {
            throw new UseApiException("No existing api "+ StringUtil.inQuotes(modelName));
        }
        return new UseSystemApiUndoable(sys);
    }
}
