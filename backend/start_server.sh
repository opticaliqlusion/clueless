#!/bin/bash
if [[ $# != 1 ]]; then
	echo 'Invalid number of arguments'
	exit 1
fi

#Identifies target environment
if [[ $1 == 'DEV' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_dev/webserver.py'
elif [[ $1 == 'TEST' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_test/webserver.py'
elif [[ $1 == 'PROD' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_prod/webserver.py'
else
	echo 'Invalid environment specified'
	exit 2
fi

echo 'Server location: ' $targetScript 

directoryName=`dirname $targetScript`
fileName=`basename $targetScript`

#Kill existing process, if it exists
pid=`ps -ef | grep "python" | grep $fileName | grep -v 'grep' | awk '{print $2}'`

if [[ -z "$pid"  ]]; then
	echo 'No existing running server found'
else
	echo 'Killing existing server with pid: ' $pid
	kill $pid
fi

#Start new server
if [ ! -f $targetScript ]; then
	echo 'Unable to locate server file'
	exit 4
else
	cd $directoryName
	echo 'Starting server'
	nohup python ./${fileName} &
	pid=$!
	cd -
fi

#Success
exit 0
