# company-matcher - A Company Matching service
This application compares the user submitted records against the all companies data and generates the matching results based on 
Jakkard Index threshold.


# Approach
The solution uses the big data processing ability of spark to process the submitted user records file against the large data of companies. For this prototype, data is available in companies.csv, however in actual application data could be either from file or database.

# Build
The project follows is maven based structure and requires maven and JDK 1.8 (or any higher version) to build and package the binary (jar). Generated jar name: "company-matcher-1.0.0-jar-with-dependencies.jar"

Checkout from URL https://github.com/saiyedriyaz/company-matcher.git 
Build the code: run command “mvn clean install”

# Set Up Execution Environment
In order to run the company-matcher application, spark libraries are required to be available in class path. 
Set up path: change the below highlighted path to match the actual path where program is going to run
export PATH=/apps/tools/jdk1.8.0_66/bin:$PATH
export SPARK_HOME=/apps/tools/spark-2.4.0-bin-hadoop2.7
export PATH=$SPARK_HOME/bin:$PATH
sh start-master.sh
sh start-slave.sh spark://<SPARK_MASTER_URL>:7077

# Prerequisite
Spark Master and slave processes are running on either same or any network devices. Provide the URL where spark master process is running. Also change the highlighted program arguments to match the actual path in test machine.

#Run Command
Format: 
spark-submit --class com.sample.matcher.app.Main --master  spark://<SPARK_MASTER_URL>:7077 --deploy-mode cluster 
--supervise company-matcher-1.0.0-jar-with-dependencies.jar  <COMPAY_DATA_FILE_WITH_PATH>   <USER_REQUEST_FILE_WITH_PATH>  <THREASOLD>  <OUTPUT_DIRECTORY_PATH>

Sample:
spark-submit --class com.sample.matcher.app.Main --master spark://<spark_master_url>:7077 --deploy-mode cluster --supervise 
company-matcher-1.0.0-jar-with-dependencies.jar /apps/example/companies.csv /apps/example/sample_user_records.csv 1 /apps/example /output/matched_records_1

#Execution:
Once spark job is submitted, the processing will be done asynchronously and data file will be prepared and stored at provided location.

