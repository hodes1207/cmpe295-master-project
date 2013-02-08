Project configuration:

1. I just combined three java projects into one project which is AnalyticEngineService.

2. There is a Visual studio project doing feature extraction which is "Master Project\cmpe295-master-project\FeatureExtractionLib". Build the project will generate a "Master Project\bin\FeatureExtraction.dll"

3. After load the AnalyticEngineService project, set the run and degud environment at Master Project\bin, so "FeatureExtraction.dll" can be found by the project's run time environment. Steps to set this:
              --> Select "Run as" --> "Run configurations" --> "Arguments" --> "Working directory" -->select Ohters: ${workspace_loc}\..\bin

4. Use 32 bit(x86) JRE instead of 64 bit, otherwise the project can not load "FeatureExtraction.dll

=================================================================================================================
Database initialization:

1. Copy IRMA image libraries, we are using only 2005

2. Open the AnalyticEngineService project, open "database" package, open "DatabaseInitiation.java"

3. The only configuration is to change the "String imageRootPath" to your local address

4. Run the script and the schemal will be built the data will be injected into the database

=================================================================================================================
The database package contains the database apis used to access CouchDB after the database is initialized (databaseAPI.java).

The code at database.DatabaseInitiation.java, Line 169 shows how to read image from local disk and then do feature extraction based on the content of the image.

