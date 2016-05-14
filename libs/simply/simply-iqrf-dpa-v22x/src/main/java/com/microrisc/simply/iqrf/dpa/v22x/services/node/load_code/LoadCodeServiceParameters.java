/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code;

import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;

/**
 * Parameters of Load Code Service.
 * 
 * @author Michal Konopa
 */
public final class LoadCodeServiceParameters {
    // source file full name
    private String fileName;
    
    // size of code to load
    private int codeSize;
    
    // start address on which will be saved data into memory
    private int startAddress;
    
    // properties of code to load
    //private LoadingCodeProperties codeProps;
    
    // loading action
    private LoadingCodeProperties.LoadingAction loadingAction;
    
    // loading content
    private LoadingCodeProperties.LoadingContent loadingContent;
            
    
    private String checkFileName(String fileName) {
        if ( fileName == null ) {
            throw new IllegalArgumentException("File name cannot be null.");
        }
        
        if ( fileName.isEmpty() ) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }
        
        // TODO: add checking of valid file name and valid file path
        
        return fileName;
    }
    
    private int checkCodeSize(int codeSize) {
        if ( codeSize < 0 ) {
            throw new IllegalArgumentException("Code size must be nonnegative.");
        }
        return codeSize;
    }
    
    private int checkStartAddress(int startAddress){
       if(startAddress < 0){
          throw new IllegalArgumentException("Start address must be between ");
       }
       return startAddress;
    }
    
    private LoadingCodeProperties.LoadingAction checkLoadingAction(
            LoadingCodeProperties.LoadingAction loadingAction
    ) {
        if ( loadingAction == null ) {
            throw new IllegalArgumentException("Loading action cannot be null.");
        }
        return loadingAction;
    }
    
    private LoadingCodeProperties.LoadingContent checkLoadingContent(
            LoadingCodeProperties.LoadingContent loadingContent
    ) {
        if ( loadingContent == null ) {
            throw new IllegalArgumentException("Type of content to load cannot be null.");
        }
        return loadingContent;
    }
    
    /*
    private LoadingCodeProperties checkCodeProps(LoadingCodeProperties props) {
        if ( props == null ) {
            throw new IllegalArgumentException("Properties of code to load cannot be null.");
        }
        return props;
    }
    */
    
    /**
     * Creates object of parameters for Load Code Service.
     * @param fileName full source file name including path  
     * @param codeSize size of code to load
     * @param startAddress start address on which will be saved data into memory
     * @param loadingAction type of action to do
     * @param loadingContent type of content to load
     * @throws IllegalArgumentException if: <br>
     *          - {@code fileName} is not valid file name <br>
     *          - {@code codeSize} is negative <br>
     *          - {@code startAddress} is negative or greater than ...<br>
     *          - {@code loadingAction} is {@code null} <br>
     *          - {@code loadingContent} is {@code null}
     */
    public LoadCodeServiceParameters( 
            String fileName, int codeSize, int startAddress,
            LoadingCodeProperties.LoadingAction loadingAction,
            LoadingCodeProperties.LoadingContent loadingContent
    ) {
        this.fileName = checkFileName(fileName);
        this.codeSize = checkCodeSize(codeSize);
        this.startAddress = checkStartAddress(startAddress);
        this.loadingAction = checkLoadingAction(loadingAction);
        this.loadingContent = checkLoadingContent(loadingContent);
        //this.codeProps = checkCodeProps(codeProps);
    }

    /**
     * @return the source file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName source file name
     */
    public void setFileName(String fileName) {
        this.fileName = checkFileName(fileName);
    }

    /**
     * @return the size of code to load
     */
    public int getCodeSize() {
        return codeSize;
    }

    /**
     * @param codeSize size of code to load
     */
    public void setCodeSize(int codeSize) {
        this.codeSize = checkCodeSize(codeSize);
    }

    /**
     * @return start address on which will be saved data into memory
     */
   public int getStartAddress() {
      return startAddress;
   }

   /**
    * @param startAddress start address on which will be saved data into memory
    */
   public void setStartAddress(int startAddress) {
      this.startAddress = startAddress;
   }

    /**
     * @return the loading action
     */
    public LoadingCodeProperties.LoadingAction getLoadingAction() {
        return loadingAction;
    }

    /**
     * @param loadingAction loading action to set
     */
    public void setLoadingAction(LoadingCodeProperties.LoadingAction loadingAction) {
        this.loadingAction = checkLoadingAction(loadingAction);
    }

    /**
     * @return the type of loading content
     */
    public LoadingCodeProperties.LoadingContent getLoadingContent() {
        return loadingContent;
    }

    /**
     * @param loadingContent type of loading content to set 
     */
    public void setLoadingContent(LoadingCodeProperties.LoadingContent loadingContent) {
        this.loadingContent = checkLoadingContent(loadingContent);
    }

}