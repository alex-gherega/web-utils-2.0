# analysis

This is an utility belt design for extracting various information during an analysis.

## Instalation
With leiningen/boot:

```clojure
[clj-http "3.7.0"]
```

## Modules
1. Web Page Test

2..n. TBD

## Web Page Test
This module can be used in conjunction with [webpagetest.org](https://www.webpagetest.org).

Once you've run your test on this site you'll have a specific URL for your test.
Plug that into this module API and extract useful info.

### What you get

For now we support only:

```clojure
(def ^:dynamic objective-values {:load-time "LoadTime"
                                 :first-byte "TTFB"
                                 :start-render "StartRender"
                                 :visual-complete "VisualComplete"
                                 :speed-index "SpeedIndex"
                                 :fst-interactive "FirstInteractive"
                                 :doc-complete "DocComplete"
                                 :fully-loaded "FullyLoaded"
                                 :bytes-in "BytesIn"})'''
```

But since this is dynamic you can extend it to your liking.

### Usage

```clojure
(analysis.web-page-test/extract-value "https://www.webpagetest.org/result/**your-hash-number**/" 1 :bytes-in)
```

In this example you're passing the URL from your test, the second argument represents the run of your test (at the momemnt you can have 1 2 or 3), and :bytes-in is a key from the objective-values map.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
