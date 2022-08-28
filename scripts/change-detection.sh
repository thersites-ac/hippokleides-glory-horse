git diff | grep "^-.*fixme"
# csplit - '/^+++/' {*} # splits STDOUT into several files based on the +++ prefix of git diff. the first file will be empty
