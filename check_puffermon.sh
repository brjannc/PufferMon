#!/bin/bash

STATUS=3
PERFDATA=$(netcat $1 $2 2> /dev/null)

if [[ $? == 0 ]]; then
    STATUS=0
    #players=3;;;0;30
    PLAYERS=$(echo ${PERFDATA} | perl -p -e 's|players=(\d+);;;0;(\d+).*|$1/$2|')
    echo "MINECRAFT OK: ${PLAYERS}|${PERFDATA}"
else
    STATUS=2
    echo "MINECRAFT CRITICAL: No response from server"
fi

exit ${STATUS}
