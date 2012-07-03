# lein-clean-m2

A Leiningen plugin to clean the local repository of unused artifacts.

This only takes the `:dependencies` and `:plugins` of a single project
into consideration; don't use this in a user account unless it's only
used for a single project.

## Usage

    $ lein clean-m2

## License

Copyright © 2012 Phil Hagelberg

Distributed under the Eclipse Public License, the same as Clojure.
