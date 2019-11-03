mongoexport --db test --collection tournaments > tournaments.json
echo "Ok tournaments"
mongoexport --db test --collection commands > commands.json
echo "Ok commands"
mongoexport --db test --collection matches > matches.json
echo "Ok matches"