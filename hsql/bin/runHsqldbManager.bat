pushd ..\..\s2-dao\src\test\resources\data-hsqldb
@java -classpath ../../../../../lib/hsqldb.jar org.hsqldb.util.DatabaseManager %1 %2 %3 %4 %5 %6 %7 %8 %9
popd
