mongoimport --db bascoopball --collection tournaments --file  tournaments.json
echo "Ok tournaments"
mongoimport --db bascoopball --collection commands --file  commands.json
echo "Ok commands"
mongoimport --db bascoopball --collection matches --file  matches.json
echo "Ok matches"