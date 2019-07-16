# Aird-SDK-Java

# Best Practise
    ### Aird Index File Suffix: .json
    ### Aird Data File Suffix: .aird
  Aird Index File and Aird Data File show be stored in the same directory with the same file name but with different suffix, so 
  that AirdScanUtil.class can scan both of the two files with the same file name;
  
  when dealing with Spectrums, we advice that you should process with Swath Window one by one so that we can control the Memory
  
#Main API Class 
 
  AirdInfo:Main Info Class that contains all the aird index file(json format) information
  SwathIndex: Contains all the information about one Swath Window
  
  AirdScanUtil : Scan for a directory for all the aird index file
  
  AirdParser: parse Spectrum with rt or parse Spectrums with Swath Window
  
