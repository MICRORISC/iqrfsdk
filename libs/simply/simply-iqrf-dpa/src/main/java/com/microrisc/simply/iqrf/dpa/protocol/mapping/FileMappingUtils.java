/*
 * Copyright 2015 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microrisc.simply.iqrf.dpa.protocol.mapping;

import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides utils methods for easier mapping from file.
 * 
 * @author Martin Strouhal
 */
public final class FileMappingUtils {
    
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(FileMappingUtils.class);

    private FileMappingUtils() {
    }   

    private static final FileMappingUtils instance = new FileMappingUtils();
    /**
     * Returns instance of {@link FileMappingUtils}.
     * @return instance
     */
    public static FileMappingUtils getInstance() {
        return instance;
    }  

    /**
     * Provides simple parsing of number form String to Integer. Supports hex, 
     * binary and decimal system.
     * @param stringValue text for parsing
     * @return parsed number
     */
    public short parseNumber(String stringValue) {
        stringValue = stringValue.trim();
        if (stringValue.startsWith("0x")) {
            return Short.parseShort(stringValue.substring(2), 16);
        } else if (stringValue.startsWith("0b")) {
            return Short.parseShort(stringValue.substring(2), 2);
        } else {
            return Short.parseShort(stringValue);
        }
    }

    /**
     * Get instance of {@link AbstractConvertor} by method annotationed by
     * {@link ConvertorFactoryMethod}. This method <b>must</b> return instance
     * of {@link AbstractConvertor}.
     * <p>
     * @param stringConvertor full class name of convertor
     * @return instance of {@link AbstractConvertor}
     * @throws Exception if some errror has occured, eg. non-existing class or
     * class without annotation
     */
    public AbstractConvertor getConvertor(String stringConvertor) throws Exception {
        logger.debug("getConvertor - start: stringConvertor={}", stringConvertor);
        Class classConvertor;
        try {
            classConvertor = Class.forName(stringConvertor);
        } catch (ClassNotFoundException ex) {
            String txt = "Class for convertor " + stringConvertor + " was not found." + ex;
            logger.error(txt);
            throw new Exception(txt);
        }

        Method[] methods = classConvertor.getMethods();
        for (Method method : methods) {
            Annotation annot = method.getAnnotation(ConvertorFactoryMethod.class);

            if (/*method.getReturnType().isAssignableFrom(AbstractConvertor.class)
                     &&*/annot instanceof ConvertorFactoryMethod) {
                AbstractConvertor convertor = null;
                try {
                    convertor = (AbstractConvertor) method.invoke(null, null);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    String txt = "It wasn't found method with ConvertorFactoryAnnotation "
                            + "for " + stringConvertor + ". " + ex;
                    logger.error(txt);
                    throw new Exception(txt);
                }
                logger.debug("getConvertor - end: convertor={}", convertor);
                return convertor;
            }
        }
        String txt = "It wasn't found method with ConvertorFactoryAnnotation "
                + "for " + stringConvertor;
        logger.error(txt);
        throw new Exception(txt);
    }

    /**
     * Checks if node convertable to node and if it's, returns converted as
     * element.
     * <p>
     * @param node to checking and converting
     * @return node as element
     * @throws Exception if node isn't convertable to element, eg. if XML file
     * is inconsistent
     */
    public Element convertNode(Node node) throws Exception {
        if (node == null) {
            String txt = "Parsed XML node is null.";
            logger.error(txt);
            throw new Exception(txt);
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            String txt = "Parsed XML node isn't element node.";
            logger.error(txt);
            throw new Exception(txt);
        }
        return (Element) node;
    }
       
    /**
     * Returns zeroth Node from sourceElement with name choosed by elementName. 
     * It's useful for shorter writing
     * 
     * @param sourceElement from which will be returned node
     * @param elementName naome of returned element
     * @return node
     * @throws Exception if some error has been occured
     */
    public Node getNode(Element sourceElement, String elementName) throws Exception {
        NodeList list = sourceElement.getElementsByTagName(elementName);
        if (list.getLength() > 0) {
            return list.item(0);
        }
        String txt = "Node with name " + elementName + " doesn't exist in " + sourceElement;
        logger.warn(txt);
        throw new Exception(txt);
    }
}
