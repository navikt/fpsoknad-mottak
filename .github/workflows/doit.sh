timestamp=`git log -n1 --format="%at"`
my_date=`perl -e "print scalar localtime ($timestamp)"`
git log -n1 --pretty=format:"Blah-blah $my_date"
