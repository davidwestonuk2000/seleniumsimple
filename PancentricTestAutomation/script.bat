call mvn install:install-file -Dfile=Libraries/selenium-server-standalone-2.53.1.jar -DgroupId=org.seleniumhq.selenium -DartifactId=selenium-server-standalone -Dversion=2.53.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.1.0 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\wlclient.jar -DgroupId=oracle -DartifactId=wlclient -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\weblogic.jar -DgroupId=oracle -DartifactId=weblogic -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\oracle.servicebus.services.core.jar -DgroupId=oracle.servicebus.services -DartifactId=core -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\oracle.servicebus.resources.service.jar -DgroupId=oracle.servicebus.resources -DartifactId=service -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\oracle.servicebus.kernel-wls.jar -DgroupId=oracle.servicebus -DartifactId=kernal-wls -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\oracle.servicebus.kernel-api.jar -DgroupId=oracle.servicebus -DartifactId=kernal-api -Dversion=12.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\webdriver-accessibility-1.1.0-SNAPSHOT.jar -DgroupId=com.accessibility -DartifactId=webdriver-accessibility -Dversion=1.1.0-SNAPSHOT -Dpackaging=jar

call mvn install:install-file -Dfile=Libraries\selenium-server-standalone-2.53.1.jar -DgroupId=org.seleniumhq.selenium -DartifactId=selenium-server-standalone -Dversion=2.53.1 -Dpackaging=jar