#!/bin/bash
if [[ $# != 1 ]]; then
	echo 'Invalid number of arguments'
	exit 1
fi

#Identifies target environment
if [[ $1 == 'DEV' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_dev/app.py'
elif [[ $1 == 'TEST' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_test/app.py'
elif [[ $1 == 'PROD' ]]; then
	targetScript='/usr/share/epioneers/clueless/server_prod/app.py'
else
	echo 'Invalid environment specified'
	exit 2
fi

echo 'Server location: ' $targetScript 

#File contains list of currently running servers
running_servers='/usr/share/epioneers/clueless/env/running_servers'
pid=`cat $running_servers | grep "DEV" | awk '{print $2}'`

#Kill existing process, if it exists
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
	cd `dirname $targetScript`
	echo 'Starting server'
	nohup ./app.py &
	pid=$!
	cd -
fi

#Put pid back into list
grep -v $1 $running_servers > tmpRunningServers
mv tmpRunningServers $running_servers
echo $1 $pid >> $running_servers

#Success
exit 0
