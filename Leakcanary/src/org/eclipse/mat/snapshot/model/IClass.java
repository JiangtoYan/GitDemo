/**
 * ****************************************************************************
 * Copyright (c) 2008 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAP AG - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.mat.snapshot.model;

import java.util.List;
import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.util.IProgressListener;

/**
 * Interface for a class instance in the heap dump.
 *
 * @noimplement
 */
public interface IClass extends IObject {
  String JAVA_LANG_CLASS = "java.lang.Class"; //$NON-NLS-1$
  String JAVA_LANG_CLASSLOADER = "java.lang.ClassLoader"; //$NON-NLS-1$

  /**
   * Returns the fully qualified class name of this class.
   */
  String getName();

  /**
   * Returns the number of instances of this class present in the heap dump.
   */
  int getNumberOfObjects();

  /**
   * Ids of all instances of this class (an empty array if there are no instances of the class)
   */
  int[] getObjectIds() throws SnapshotException;

  /**
   * Returns the id of the class loader which loaded this class.
   */
  int getClassLoaderId();

  /**
   * Returns the address of the class loader which loaded this class.
   */
  long getClassLoaderAddress();

  /**
   * Returns field descriptors for all member variables of instances of this
   * class.
   */
  List<FieldDescriptor> getFieldDescriptors();

  /**
   * Returns the static fields and it values.
   */
  List<Field> getStaticFields();

  /**
   * Returns the heap size of one instance of this class. Not valid if this
   * class represents an array.
   */
  int getHeapSizePerInstance();

  /**
   * Returns the retained size of all objects of this instance including the
   * class instance.
   */
  long getRetainedHeapSizeOfObjects(boolean calculateIfNotAvailable,
                                    boolean calculateMinRetainedSize, IProgressListener listener) throws SnapshotException;

  /**
   * Returns the id of the super class. -1 if it has no super class, i.e. if
   * it is java.lang.Object.
   */
  int getSuperClassId();

  /**
   * Returns the super class.
   */
  IClass getSuperClass();

  /**
   * Returns true if the class has a super class.
   */
  boolean hasSuperClass();

  /**
   * Returns the direct sub-classes.
   */
  List<IClass> getSubclasses();

  /**
   * Returns all sub-classes including sub-classes of its sub-classes.
   */
  List<IClass> getAllSubclasses();

  boolean doesExtend(String className) throws SnapshotException;

  /**
   * Returns true if the class is an array class.
   */
  boolean isArrayType();
}
