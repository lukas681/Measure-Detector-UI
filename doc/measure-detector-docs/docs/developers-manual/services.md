# Services

## Back-End Services

Services are the "way-to-go" for implementing new functionalities as they are located intermediate between the data persistence and controller layers. Currently, the following services are implemented

* **EditingService** providing most of the functionalities that are used for managing an edition in a specific project. Includes also the connection between the actual PDF file and the database 
* **EditingFileManagemetService** manage most of the interconnectivity between storing the PDF/PNG files related to a specific Edition

### EditingService 

The editing service manages the editing process.

#### Splitting Editions

In order to use the edition with the measure detector later on, we have to split them into seperate parts, each consisting of one page and stored as a PNG file. Therefore, *Apache PDFBox* is used, a library fully capable of managing PDF files both in-memory and on the local file system. We do not want to store the images/PDF files in the database directly, because these can let the database grow a lot in file size. Therefore, we store these files in a seperate folder on the servers' file system.

## Front-End Server