# Open Source Clojure Projects

### _write some code, live forever*_

_\*maybe? you won't know until you try_

This is the source code for http://open-source.braveclojure.com/, a
directory of active open-source Clojure projects.

The code is, well... not great? Really weird? In a state that only
works for one person? Check out the issues if you'd like to help make
it better :)

## Run it locally

This project uses a GitHub repo as its database (you can view the
production repo here:
https://github.com/braveclojure/open-source-projects/). To run it
locally, first set these environment variables:

```
export OPEN_SOURCE_GITHUB_PROJECT_USER=your-username
export OPEN_SOURCE_GITHUB_PROJECT_REPO=your-project-repo
export OPEN_SOURCE_GITHUB_OAUTH_TOKEN=your-oauth-token
```

[Learn how to create a GitHub oauth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/).

Then, install [SassC](https://github.com/sass/sassc), a command line
tool for compiling [Sass](http://sass-lang.com/).

Next, `boot dev` will start the server at
http://localhost:3000. Boot's super awesome for ClojureScript
development and has figwheel-like auto-reloading. Make a change and
see it appear!

## Brief Code Overview

TODO

## Deploying

TODO

## Collaboration

Feature requests and TODOs tracked in
[issues](https://github.com/braveclojure/open-source/issues).

Pull requests welcome!
