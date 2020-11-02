# AirdPro
  You should use the AirdPro client to transfer the vendor files into Aird format.
  AirdPro:

# Best Practise
    Aird Index File Suffix: .json
    Aird Data File Suffix: .aird
  Aird Index File and Aird Data File show be stored in the same directory with the same file name but with different suffix, so 
  that AirdScanUtil.class can scan both of the two files with the same file name;
  
  when dealing with Spectrums, we advice that you should process with Swath Window one by one so that we can control the Memory
  

#Maven 
    <dependency>
        <groupId>net.csibio.aird</groupId>
        <artifactId>aird-sdk</artifactId>
        <version>1.0.0</version>
    </dependency>
    
#Main API Class 
 
  AirdManager is the entry level class. You can also use the Parsers under the parser package.
  Aird-SDK 1.0.X is currently support three types of MS file.
  1. DIA/SWATH
  2. DDA
  3. Common type like mzXML
  
  
# Refrence

Paper see https://www.biorxiv.org/content/10.1101/2020.10.14.338921v1