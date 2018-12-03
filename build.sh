if [ $# -eq 0 ] ; then
  mvn -Dspring.profiles.active=test clean package -U
else
  if [ $1 = "-q" ] ; then
    mvn -Dmaven.test.skip=true clean package -U
  else
    echo "build.sh [-q]"
    exit 0
  fi
fi

if [ $? -ne 0 ] ; then
  echo "mvn package error"
  exit -1
fi

rm -rf output
mkdir -p output/lib
mkdir -p output/bin
mkdir -p output/log
cp target/weixin-java-mp-demo-springboot-1.0.0-SNAPSHOT.jar output/lib
cp start.sh output/bin
cp -r conf output/