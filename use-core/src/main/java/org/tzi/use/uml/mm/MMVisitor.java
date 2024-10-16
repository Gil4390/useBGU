/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.tzi.use.uml.mm;

import org.tzi.use.uml.mm.commonbehavior.communications.MSignal;
import org.tzi.use.uml.ocl.type.EnumType;


/**
 * Visitor interface for model elements.
 *
 * @author      Mark Richters 
 */
public interface MMVisitor {
	void visitAnnotation(MElementAnnotation a);
    void visitAssociation(MAssociation e);
    void visitAssociationClass( MAssociationClass e );
    void visitAssociationEnd(MAssociationEnd e);
    void visitAttribute(MAttribute e);
    void visitClass(MClass e);
    void visitClassInvariant(MClassInvariant e);
    void visitGeneralization(MGeneralization e);
    void visitModel(MModel e);
    void visitOperation(MOperation e);
    void visitPrePostCondition(MPrePostCondition e);
	void visitSignal(MSignal mSignalImpl);
	void visitEnum(EnumType enumType);
    void visitMediator(MMediator mMediator);
    void visitClabject(MClabject mClabject);
    void visitAssoclink(MAssoclink mAssoclink);
}
