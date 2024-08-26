set -e

function print() {
    printf "\033[1;34m$1\033[0m\n"
}

print "Starting the app 🏎️"

java -XX:-UseJVMCICompiler -Xmx512m -jar ./target/demo-0.0.1-SNAPSHOT.jar &
export PID=$!
psrecord $PID --plot "$(date +%s)-jit-c2.png" --max-cpu 2200 --max-memory 900 --include-children &

sleep 4
print "Done waiting for startup..."

print "Executing warmup load"
hey -n=250000 -c=8 http://localhost:8080/hello

print "Executing benchmark load"
hey -n=250000 -c=8 http://localhost:8080/hello

print "JVM run done!🎉"
kill $PID
sleep 1