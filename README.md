# elasticsearch-lang-clojure

A an elasticsearch plugin written in clojure that provides clojure as
a scripting language for elasticsearch queries

## Usage

```
lein uberjar
plugin --url file://`pwd`/target/elasticsearch-lang-clojure-0.3.0-SNAPSHOT-standalone.jar  -i elasticsearch-lang-clojure
```

Scripts are implicitly wrapped in a `do` form and evaluate with `env` bound to
a map holding script params and special context-based maps (e.g. _source, ctx, etc.)

## License

Copyright © 2013 Kevin Downey

Distributed under the Eclipse Public License, the same as Clojure.
