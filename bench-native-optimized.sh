set -e

function print() {
    printf "\033[1;35m$1\033[0m\n"
}

print "Starting the native app 🚀"

./target/demo-optimized -Xmx512m &
export PID=$!
psrecord $PID --plot "$(date +%s)-graalvm-native.png" --max-cpu 2200 --max-memory 900 --include-children &

sleep 2
print "Done waiting for startup..."

print "Executing warmup load"
hey -n=250000 -c=8 http://localhost:8080/hello

print "Executing benchmark load"
hey -n=250000 -c=8 http://localhost:8080/hello

print "Native run done!🎉"
kill $PID
sleep 1