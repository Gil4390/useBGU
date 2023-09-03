package org.tzi.use.uml.ocl.expr;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassImpl;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.StringUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExpMultiNavigation extends Expression{

    private final MModel fSrcModel;
    private final MModel fDstModel;
    private final MNavigableElement fSrc;
    private final MNavigableElement fDst;
    private final Expression fObjExp;
    private final Expression[] qualifierExpressions;


    public ExpMultiNavigation(Expression objExp,
                              MModel srcModel, MModel dstModel,
                              MNavigableElement src, MNavigableElement dst,
                              List<Expression> theQualifierExpressions)  throws ExpInvalidException
    {
        // set result type later
        super(null);

        if (theQualifierExpressions == null) {
            this.qualifierExpressions = new Expression[0];
        } else {
            this.qualifierExpressions = theQualifierExpressions.toArray(new Expression[theQualifierExpressions.size()]);
        }

        if ( !objExp.type().isKindOfClassifier(Type.VoidHandling.EXCLUDE_VOID) )
            throw new ExpInvalidException(
                    "Target expression of navigation operation must have " +
                            "object type, found `" + objExp.type() + "'." );

        if (!src.hasQualifiers() && qualifierExpressions.length > 0) {
            throw new ExpInvalidException("The navigation end " + StringUtil.inQuotes(dst.nameAsRolename()) +
                    " has no defined qualifiers, but qualifer values were provided.");
        }

        setResultType( dst.getType( objExp.type(), src, qualifierExpressions.length > 0 ) );

        this.fSrcModel = srcModel;
        this.fDstModel = dstModel;
        this.fSrc = src;
        this.fDst = dst;
        this.fObjExp = objExp;
    }

    @Override
    public Value eval(EvalContext ctx) {
        ctx.enter(this);
        Value res = UndefinedValue.instance;
        final Value val = fObjExp.eval(ctx);

        MNavigableElement convertedSrc = this.fSrc;
        MNavigableElement convertedDst = this.fDst;

        Map<String, MClass> classes = ctx.preState().system().model().classesMap();
        if (ctx.preState() != null) {
            if (!classes.containsKey(this.fSrc.cls().name())) {
                // in conversion
                String srcModelName = this.fSrc.cls().model().name();
                String srcClassName = this.fSrc.cls().name();
                String convertedSrcClassName = srcModelName + "@" + srcClassName;
                Map<String, ? extends MNavigableElement> ends1 = classes.get(convertedSrcClassName).navigableEnds();

                String dstModelName = this.fDst.cls().model().name();
                String dstClassName = this.fDst.cls().name();
                String convertedDstClassName = dstModelName + "@" + dstClassName;
                Map<String, ? extends MNavigableElement> ends2 = classes.get(convertedDstClassName).navigableEnds();

                for (Map.Entry<String, ? extends MNavigableElement> entry : ends1.entrySet()) {
                    MNavigableElement value = entry.getValue();
                    if (value.cls().name().equals(convertedDstClassName)
                            && convertedDst.nameAsRolename().equals(value.nameAsRolename())) {
                        convertedDst = value;
                        break;
                    }
                }

                for (Map.Entry<String, ? extends MNavigableElement> entry : ends2.entrySet()) {
                    MNavigableElement value = entry.getValue();
                    if (value.cls().name().equals(convertedSrcClassName)
                            && convertedSrc.nameAsRolename().equals(value.nameAsRolename())) {
                        convertedSrc = value;
                        break;
                    }
                }
            }
        }


        // if we don't have an object we can't navigate
        if (! val.isUndefined() ) {
            // get the object
            final ObjectValue objVal = (ObjectValue) val;
            final MObject obj = objVal.value();
            final MSystemState state = isPre() ? ctx.preState() : ctx.postState();
            final Type resultType = type();


            // get objects at association end
            List<Value> qualifierValues = new LinkedList<Value>();

            for (Expression exp : this.qualifierExpressions) {
                qualifierValues.add(exp.eval(ctx));
            }

            List<MObject> objList = obj.getNavigableObjects(state, convertedSrc, convertedDst, qualifierValues);
            if (resultType.isTypeOfClass() ) {
                if (objList.size() > 1 )
                    throw new MultiplicityViolationException(
                            "expected link set size 1 at " +
                                    "association end `" + fDst +
                                    "', found: " +
                                    objList.size());
                if (objList.size() == 1 ) {
                    res = new ObjectValue((MClass)type(), objList.get(0));
                }
            } else if (resultType.isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)) {
                CollectionType ct = (CollectionType)resultType;
                res = ct.createCollectionValue(oidsToObjectValues(state, objList));
            } else
                throw new RuntimeException("Unexpected association end type `" +
                        resultType + "'");
        }

        ctx.exit(this, res);
        return res;
    }

    private Value[] oidsToObjectValues(MSystemState state, List<MObject> objList) {
        Value[] res = new ObjectValue[objList.size()];
        int i = 0;

        for (MObject obj : objList) {
            MObjectState objState = obj.state(state);
            if (objState != null )
                res[i++] = new ObjectValue(obj.cls(), obj);
        }
        return res;
    }

    @Override
    protected boolean childExpressionRequiresPreState() {
        return false;
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return null;
    }

    @Override
    public void processWithVisitor(ExpressionVisitor visitor) {

    }
}