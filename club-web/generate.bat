org.jooq.util.GenerationTool /jooq-config.xml

Be sure that these elements are located on the classpath:
The XML configuration file
jooq-3.4.2.jar, jooq-meta-3.4.2.jar, jooq-codegen-3.4.2.jar
The JDBC driver you configured
A command-line example (For Windows, unix/linux/etc will be similar)
Put the property file, jooq*.jar and the JDBC driver into a directory, e.g. C:\temp\jooq
Go to C:\temp\jooq
Run java -cp jooq-3.4.2.jar;jooq-meta-3.4.2.jar;jooq-codegen-3.4.2.jar;[JDBC-driver].jar;. org.jooq.util.GenerationTool /[XML file]
Note that the property file must be passed as a classpath resource