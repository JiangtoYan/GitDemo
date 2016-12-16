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

/**
 * Interface for primitive arrays in the heap dump.
 *
 * @noimplement
 */
public interface IPrimitiveArray extends IArray {
  /**
   * Primitive signatures.
   */
  byte[] SIGNATURES = {
      -1, -1, -1, -1, (byte) 'Z', (byte) 'C', (byte) 'F', (byte) 'D', (byte) 'B', (byte) 'S',
      (byte) 'I', (byte) 'J'
  };

  /**
   * Element sizes inside the array.
   */
  int[] ELEMENT_SIZE = { -1, -1, -1, -1, 1, 2, 4, 8, 1, 2, 4, 8 };

  /**
   * Display string of the type.
   */
  @SuppressWarnings("nls")
  String[] TYPE = {
      null, null, null, null, "boolean[]", "char[]", "float[]", "double[]", "byte[]", "short[]",
      "int[]", "long[]"
  };

  /**
   * Java component type of the primitive array.
   */
  Class<?>[] COMPONENT_TYPE = {
      null, null, null, null, boolean.class, char.class, float.class, double.class, byte.class,
      short.class, int.class, long.class
  };

  /**
   * Returns the {@link IObject.Type} of the primitive array.
   */
  int getType();

  /**
   * Returns the component type of the array.
   */
  Class<?> getComponentType();

  /**
   * Returns the Object at a given index.
   */
  Object getValueAt(int index);

  /**
   * Get the primitive Java array. The return value can be casted into the
   * correct component type, e.g.
   *
   * <pre>
   * if (char.class == array.getComponentType())
   * {
   *     char[] content = (char[]) array.getValueArray();
   *     System.out.println(content.length);
   * }
   * </pre>
   *
   * The return value must not be modified because it is cached by the heap
   * dump adapter. This method does not return a copy of the array for
   * performance reasons.
   */
  Object getValueArray();

  /**
   * Get the primitive Java array, beginning at <code>offset</code> and
   * <code>length</code> number of elements.
   * <p>
   * The return value must not be modified because it is cached by the heap
   * dump adapter. This method does not return a copy of the array for
   * performance reasons.
   */
  Object getValueArray(int offset, int length);
}
