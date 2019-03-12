#!/bin/sh
option=
for arg in "$@"
do
    key=${arg%%=*}
    value=${arg#*=}
    case $key in
    --configService)
        configService=$value
        ;;
    --config)
        config=$value
        ;;
    --local)
        local=$value
        ;;
    --log4j)
        log4j=$value
        ;;
    --env)
        env=$value
        ;;
    *)
        option="$option $key=$value"
        ;;
    esac
done

if [ ! -n $configService ];then
    echo "please input configService url"
    exit 2
fi
if [ ! -n $config ];then
    echo "please input config filename"
    exit 2
fi
if [ ! -n $local ];then
    echo "--local must select dev/gqc/prde4/prde11"
    exit 2
fi
if [ ! -n $log4j ];then
    echo "--please input log4j config filename"
    exit 2
fi

# spring boot config
wget --no-check-certificate -O ./application.properties $configService/$config
if [ ! -f application.properties ];then
    echo "not find publicConfig application.properties"
    exit 2
fi
# log4j config
wget --no-check-certificate -O ./log4j.properties $configService/$log4j
if [ ! -f log4j.properties ];then
    echo "not find publicConfig log4j.properties"
    exit 2
fi
# jvm config
wget --no-check-certificate -O ./env.properties $configService/$env
source ./env.properties

java ${JAVA_OPTS} -Dlog4j.configuration="file:./log4j.properties" -jar feedflow-job.jar --local=$local $option
