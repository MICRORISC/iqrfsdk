/* 
 * Copyright 2014 MICRORISC s.r.o.
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

package com.microrisc.simply.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Configuration's reader.
 * <p>
 * IMPORTANT ATTENTION: 
 * The currently only supported format is: <b>.properties</b> file type.
 * 
 * @author Michal Konopa
 */
public class ConfigurationReader {
    /** Supported configuration file types. */
    private enum FileType {
        PROPERTIES;
    }
    
    /** Extensions of supported configuration file types. */
    private enum FileTypeExtension {
        PROPERTIES    ("properties", FileType.PROPERTIES);
        
        private final String extension;
        private final FileType fileType;
        
        private FileTypeExtension(String extension, FileType fileType) {
            this.extension = extension;
            this.fileType = fileType;
        }
        
        public String getExtension() {
            return extension;
        }
        
        public FileType getFileType() {
            return fileType;
        }
    }
    
    /**
     * Returns type of specified configuration file.
     * @param file file, whose type to determine
     * @return type of specified configuration file
     */
    private static FileType getFileType(String file) throws ConfigurationReaderException {
        int dotPos = file.lastIndexOf('.');
        if ( dotPos == -1 ) {
            throw new ConfigurationReaderException("Cannot determine type of file.");
        }
        
        String inputFileExt = file.substring(dotPos+1);
        
        for ( FileTypeExtension fileExtension : FileTypeExtension.values() ) {
            if ( fileExtension.getExtension().equals(inputFileExt) ) {
                return fileExtension.getFileType();
            }
        }
        
        throw new ConfigurationReaderException("Unknown file type: " + inputFileExt);
    } 
    
    /**
     * Creates configuration from specified .property file.
     * @param file source property file
     * @return configuration
     */
    private static Configuration createConfigurationFromProperties(String file) 
            throws ConfigurationException {
        return new PropertiesConfiguration(file);
    }
    
    
    /**
     * Reads configuration data from specified file, creates {@code Configuration} object, 
     * and returns it. 
     * <p>
     * The currently only supported format is: <b>.properties</b> file type. If 
     * the specified file has another format, {@code ConfigurationReaderException}
     * is thrown.
     * 
     * @param file source configuration file
     * @return configuration object corresponding to configuration read from specified file
     * @throws IllegalArgumentException if {@code file} is {@code null}
     * @throws ConfigurationReaderException if type of configuration file could not be determined
     * @throws ConfigurationException if an error has been detected during processing
     *         of specified configuration file
     */
    public static Configuration fromFile(String file) 
            throws ConfigurationReaderException, ConfigurationException 
    {
        if ( file == null ) {
            throw new IllegalArgumentException("File name cannot be null");
        }
       
        FileType inputFileType = getFileType(file);
        switch ( inputFileType ) {
            case PROPERTIES:
                return createConfigurationFromProperties(file);
            default:
                throw new ConfigurationReaderException("Not supported configuration file type");
        }
    }
}
